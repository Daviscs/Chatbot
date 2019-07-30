var mysql = require("mysql");
var config = require("./config.json");
var AWS = require("aws-sdk");
var convertTime = require("convert-time");
const moment = require("moment");

var openTimeArray,
  closeTimeArray,
  doctorArray,
  doctorTypeArray,
  doctorTypeId,
  datesArray,
  startTimeUn,
  endTimeUn,
  startAppointment,
  endAppointment,
  openAndCloseId,
  doctorNames,
  address,
  timeFinished = false,
  data = {},
  unavaiableData = {},
  operationData = {},
  doctorData = {},
  scheduleID,
  addressData = {},
  newTime;

exports.handler = (event, context, callback) => {
  var appointmentType = event["currentIntent"]["slots"]["AppointmentType"];
  var date = event["currentIntent"]["slots"]["Date"];
  var time = event["currentIntent"]["slots"]["Time"];
  var doctorType = event["currentIntent"]["slots"]["DoctorType"];
  var confirmTime = event["currentIntent"]["slots"]["ConfirmTime"];
  var phoneNum = event["currentIntent"]["slots"]["PhoneNumber"];
  
  //var zip = event['currentIntent']['slots']['Zip']

  var pool = mysql.createPool({
    //database credentials
    host: config.dbhost,
    user: config.dbuser,
    password: config.dbpassword,
    database: config.dbname
  });

  //prevent timeout from waiting event loop
  context.callbackWaitsForEmptyEventLoop = false;

  pool.getConnection(function(err, connection) {
    //get Doctor Types
    const queryString = "Select type from Type";
    connection.query(queryString, function(error, results, fields) {
      doctorTypeArray = results.map(f => f.type);
      if (error) throw error;

      //ask for doctor type
      if (!doctorType) {
        callback(null, {
          dialogAction: {
            type: "ElicitSlot",
            message: {
              contentType: "PlainText",
              content: "What type of doctor you are looking for?"
            },
            intentName: event["currentIntent"]["name"],
            slots: event["currentIntent"]["slots"],
            slotToElicit: "DoctorType"
          }
        });
      }

      //check doctor type
      if (doctorType) {
        
        if (doctorTypeArray.includes(doctorType)) {
        } else {
          callback(null, {
            dialogAction: {
              type: "ElicitSlot",
              message: {
                contentType: "PlainText",
                content:
                  "Sorry we do not currently have a " + doctorType + ". Please choose another."
              },
              intentName: event["currentIntent"]["name"],
              slots: event["currentIntent"]["slots"],
              slotToElicit: "DoctorType"
            }
          });
        }
        // Handle error after the release.
        if (error) throw error;
      }


      //ask for appointment type
      if (!appointmentType) {
        callback(null, {
          dialogAction: {
            type: "ElicitSlot",
            message: {
              contentType: "PlainText",
              content: "What kind of appointment do you need?"
            },
            intentName: event["currentIntent"]["name"],
            slots: event["currentIntent"]["slots"],
            slotToElicit: "AppointmentType"
          }
        });
      }


      //ask for date
      if (!date) {
        callback(null, {
          dialogAction: {
            type: "ElicitSlot",
            message: {
              contentType: "PlainText",
              content: "What day should I schedule your appointment?"
            },
            intentName: event["currentIntent"]["name"],
            slots: event["currentIntent"]["slots"],
            slotToElicit: "Date"
          }
        });
      }
      
        if (!time) {
        callback(null, {
          dialogAction: {
            type: "ElicitSlot",
            message: {
              contentType: "PlainText",
              content: "What time should I schedule your appointment?"
            },
            intentName: event["currentIntent"]["name"],
            slots: event["currentIntent"]["slots"],
            slotToElicit: "Time"
          }
        });
      }
    });
    
    connection.query(
      "SELECT d.openTime, d.closeTime, d.doctor_id, d.doctorName, d.address from Doctor d join Type t on t.doctor_id = d.doctor_id where type = ?",
      [doctorType],
      function(error, results, fields) {
        doctorNames = results.map(l => l.doctorName);
        openAndCloseId = results.map(j => j.doctor_id);
        openTimeArray = results.map(f => f.openTime);
        closeTimeArray = results.map(k => k.closeTime);
        address = results.map(i=> i.address);
      
        
        for(j = 0; j < openAndCloseId.length; j++){
          var operationTimes = [];
          operationTimes.push(openTimeArray[j], closeTimeArray[j]);
          operationData[openAndCloseId[j]] = operationTimes;
        }
        
        for(k = 0; k < doctorNames.length; k++){
          doctorData[openAndCloseId[k]] = doctorNames[k];
          addressData[openAndCloseId[k]] = address[k];
        }
        // console.log("doctorData " + doctorData[3])
      }
    );
    
     connection.query(
      'Select u.startTime, u.endTime, u.doctor_id, u.unavailableDate from Unavailabilites u join Type t on t.doctor_id = u.doctor_id where type = ?',
      [doctorType],
      function(error, results, fields) {
        startTimeUn = results.map(f => f.startTime);
        endTimeUn = results.map(f => f.endTime);
         doctorId = results.map(f=> f.doctor_id);
         appointmentDate = results.map(f=>f.unavailableDate);
        
        
         var unAppointment = [];

        for (i = 0; i < appointmentDate.length; i++) {
        appointmentDate[i] = moment(appointmentDate[i]).format("MM/DD/YYYY");
        id = doctorId[i];
        currentDate = appointmentDate[i];

        if (currentDate in unavaiableData && id in unavaiableData[appointmentDate[i]]) {
          var dummy = unavaiableData[currentDate][doctorId[i]];
          unAppointment = [];
          unAppointment.push(startTimeUn[i], endTimeUn[i]);
          let merged = dummy.concat(unAppointment);
          unavaiableData[appointmentDate[i]][id] = merged;
        } else if (currentDate in unavaiableData) {
          var dummy = unavaiableData[currentDate];
          unavaiableData[appointmentDate[i]] = {};
          unAppointment = [];
          unAppointment.push(startTimeUn[i], endTimeUn[i]);
          unavaiableData[appointmentDate[i]][id] = unAppointment;
          let merged = { ...dummy, ...unavaiableData[appointmentDate[i]] };
          unavaiableData[appointmentDate[i]] = merged;
        } else {
          unavaiableData[appointmentDate[i]] = {};
          unAppointment = [];
          unAppointment.push(startTimeUn[i], endTimeUn[i]);
          unavaiableData[appointmentDate[i]][id] = unAppointment;
        }
      }
       //console.log(unavaiableData)
      }
    );




    //get start and end appointment times where type of doctor equals DoctorType and appointment date equals the date specified
    connection.query(
      "Select a.doctor_id, a.startTime, a.endTime, a.appointmentDate from Appointment a join Type t on t.doctor_id = a.doctor_id Where type = ? AND DATE_FORMAT(?, '%Y/%m/%d')",
      [doctorType, date],
      function(error, results, fields) {
        startAppointment = results.map(f => f.startTime);
        endAppointment = results.map(f => f.endTime);
        var doctorId = results.map(f=> f.doctor_id);
        var appointmentDate = results.map(f=>f.appointmentDate);
        
        var Sappointment = [];

        for (i = 0; i < appointmentDate.length; i++) {
        appointmentDate[i] = moment(appointmentDate[i]).format("MM/DD/YYYY");
        id = doctorId[i];
        currentDate = appointmentDate[i];

        if (currentDate in data && id in data[appointmentDate[i]]) {
          var dummy = data[currentDate][doctorId[i]];
          Sappointment = [];
          Sappointment.push(startAppointment[i], endAppointment[i]);
          let merged = dummy.concat(Sappointment);
          data[appointmentDate[i]][id] = merged;
        } else if (currentDate in data) {
          var dummy = data[currentDate];
          data[appointmentDate[i]] = {};
          Sappointment = [];
          Sappointment.push(startAppointment[i], endAppointment[i]);
          data[appointmentDate[i]][id] = Sappointment;
          let merged = { ...dummy, ...data[appointmentDate[i]] };
          data[appointmentDate[i]] = merged;
        } else {
          data[appointmentDate[i]] = {};
          Sappointment = [];
          Sappointment.push(startAppointment[i], endAppointment[i]);
          data[appointmentDate[i]][id] = Sappointment;
        }
      }
       //console.log(data)
        
        //check to see if time is within ours
        if(time && !confirmTime && !timeFinished){
          // console.log(withinHours(time));
          if (withinHours(time)) {
          } 
          else {
             timeConverted = moment(time, 'HH:mm').format('hh:mm a');
              timeConverted2 = moment(openTimeArray[0], 'HH:mm').format('hh:mm a');
               timeConverted3 = moment(closeTimeArray[0], 'HH:mm').format('hh:mm a');
            callback(null, {
              dialogAction: {
                type: "ElicitSlot",
                message: {
                  contentType: "PlainText",
                  content: "Sorry " + timeConverted + " is outstide of operating hours. Choose another time between " + timeConverted2 + " and " + timeConverted3
                },
                intentName: event["currentIntent"]["name"],
                slots: event["currentIntent"]["slots"],
                slotToElicit: "Time"
              }
            });
          }
          //check to see if time is available
          if (isAvailable(time) && !confirmTime) {
          // timeConverted = moment(time, 'HH:mm').format('hh:mm a');
          newTime = time;
            timeFinished = true;
             callback(null, {
            dialogAction: {
              type: "ElicitSlot",
              message: {
                contentType: "PlainText",
                content:
                  "That time is available with doctor " + doctorData[scheduleID] + " at " + addressData[scheduleID] + 
                  " is that okay? Please reply with yes or no."
              },
              intentName: event["currentIntent"]["name"],
              slots: event["currentIntent"]["slots"],
              slotToElicit: "ConfirmTime"
            }
          });
          } else{
            var increment = time;
            var decrement = time;
            var found = false;
            var inBounds1 = true;
            var inBounds2 = true;
            
              while (!found) {
              increment = incremental(increment);
              decrement = decremental(decrement);
              if (!withinHours(increment)) {
                inBounds1 = false;
              }
              if (!withinHours(decrement)) {
                inBounds2 = false;
              }
              
              if (isAvailable(increment)) {
              
                if(withinHours(increment)){
                found = true;
                timeFinished = true;
                newTime = increment;

                break;
                }
                break;
              }
              if (isAvailable(decrement)) {
             
                if(withinHours(decrement)){

                found = true;
                timeFinished = true;
                newTime = decrement;
                break;
                }
                break;
              }
                if (!inBounds1 && !inBounds2) {
                  increment = time;
                  decrement = time;
                  inBounds1 = true;
                  inBounds2 = true;
                  
                  date = moment(date).add(1, "days");
                  date = moment(date).format("YYYY-MM-DD");
                  
                if(isAvailable(time)){
                   found = true;
                   timeFinished = true;
                   
                }
                break;
              
              }
            }
                callback(null, {
            dialogAction: {
              type: "ElicitSlot",
              message: {
                contentType: "PlainText",
                content:
                  "Sorry that time is not available, we have " +
                  newTime +
                  ", on " +
                  date + " with doctor " + doctorData[scheduleID] +" at " + addressData[scheduleID] +
                  " is that okay? Please reply with yes or no."
              },
              intentName: event["currentIntent"]["name"],
              slots: event["currentIntent"]["slots"],
              slotToElicit: "ConfirmTime"
            }
          });
          }
            
      
        }
        
  if(confirmTime == "yes"){  
    // if(!phoneNum){
    //       callback(null, {
    //         dialogAction: {
    //           type: "ElicitSlot",
    //           message: {
    //             contentType: "PlainText",
    //             content:
    //               "What is your phone number?"
    //           },
    //           intentName: event["currentIntent"]["name"],
    //           slots: event["currentIntent"]["slots"],
    //           slotToElicit: "PhoneNumber"
    //         }
    //       });
    // }
    if (time && appointmentType && date && timeFinished == true) {
      timeConverted = moment(time, 'HH:mm').format('hh:mm a');
      callback(null, {
              dialogAction: {
                type: "Close",
                fulfillmentState: "Fulfilled",
                message: {
                  contentType: "PlainText",
                  content:
                    "We will see you at " +
                    newTime +
                    " on " +
                    date +
                    " for a " +
                    appointmentType + " with doctor " + doctorData[scheduleID]
                }
              }
            });
      
    
      endTime = incremental(time);
      date = moment(date).format("YYYY-MM-DD");
      const queryString =
        "Insert into Appointment(doctor_id, startTime, endTime, appointmentType, appointmentDate, patientNum) Values (?, ?, ?, ?, ?, 7571234567)";
      connection.query(
        queryString,
        [scheduleID, time, endTime, appointmentType, date],
        function(error, results, fields) {
          connection.release();
          // Handle error after the release.
          if (error) throw error;
          process.exit();
        }
      );
    }
  }
  else if(confirmTime == "no"){
     callback(null, {
              dialogAction: {
                type: "Close",
                fulfillmentState: "Fulfilled",
                message: {
                  contentType: "PlainText",
                  content:
                    "Okay, we will cancel your appointment, please call again."
                }
              }
            });
  }
        
      });
  });
  

  var isTrue;
  var isTrue2;

  function incremental(time) {
   time = moment(time, "H:mm A").add(30, "minutes").format("HH:mm");
   return time;
  }

  function decremental(time) {
  time = moment(time, "H:mm A").subtract(30, "minutes").format("HH:mm");
   return time;
  }

  function isAvailable(time) {
  date = moment(date).format("MM/DD/YYYY");
  
   idNum = 0;
   isTrue = true;
   isTrue2 = true;
   isTrue3 = true;
    
    while(isTrue3){
     // console.log(date);
      
      if(unavaiableData[date] == undefined){
        console.log("is Good");
        isTrue = false;
      }
      else{
        if(doctorId[idNum] in unavaiableData[date]){
        for(l = 0; l < unavaiableData[date][doctorId[idNum]].length-1; l+2){
              if(unavaiableData[date][doctorId[idNum]][l] <= time && time < unavaiableData[date][doctorId[idNum]][l+1]){
              isTrue = true;
                if(l+1 ==  unavaiableData[date][doctorId[idNum]].length-1 && isTrue == true){
                console.log( unavaiableData[date][doctorId[idNum]]);
                console.log("un")
                break;
                }
            }
            else{
              isTrue = false;
              break;
            }
          }
        }
      }
      
      if(data[date] == undefined){
       console.log("is kood");
       isTrue2 = false;
      }
      else{
        if(doctorId[idNum] in data[date]){
       // console.log(data[date])
       
        for(l = 0; l < data[date][doctorId[idNum]].length-1; l+2){
            if(data[date][doctorId[idNum]][l] <= time && time < data[date][doctorId[idNum]][l+1]){
            isTrue2 = true;
              if(l+1 ==  data[date][doctorId[idNum]].length-1 && isTrue2 == true){
              console.log( data[date][doctorId[idNum]]);
              console.log("av")
              break;
              }
          }
          else{
           console.log("Good2")
            isTrue2 = false;
            break;
          }
        }
      }
        
      }
  
   
     if(idNum < doctorId.length-1) {
        idNum++;
     }
     else{
      break;
      }
    }
    
    if(isTrue == false && isTrue2 == false){
      console.log("Everything is good");
      if(data[date] == undefined && unavaiableData[date] == undefined){
        scheduleID = doctorId[0];
      }
      else{
        scheduleID = doctorId[idNum];
      }
      isTrue3 = false;
      return true;
    }
    else{
      console.log("Everything is bad");
      return false;
    }
    
  }

  function withinHours(time) {
     var objKeys = Object.keys(operationData);
  
      for(k = 0; k < objKeys.length; k++) {
        for(l = 0; l < operationData[objKeys[k]].length-1; l++){
          if(operationData[objKeys[k]][l] <= time && time < operationData[objKeys[k]][l+1]){
          //  console.log("inside operations");
            return true;
        }
          else{
            return false;
          }
        }
        
      }
  }

};




