/*------------------------------------------------------------------------------
-- SOURCE FILE: Server.js - Node Server 
--
-- PROGRAM: COMP4985 A3 
--
-- DATE: Mar. 23, 2016
--
-- REVISIONS: 
-- Version 1.0 - [EY] - 2016/Mar/23 - Created simple server 
-- Version 1.0 - [EY] - 2016/Mar/26 - Created server-db writes 
--
-- DESIGNER: Eva Yu
--
-- PROGRAMMER: Eva Yu
--
-- NOTES:
-- Simple node server that will read incoming data from port 7424
-- and store all the data in a json file
------------------------------------------------------------------------------*/
const net = require('net');
var mysql      = require('mysql');
const port = 7424;

/****** ONLY FOR FILE WRITES *******/
//var fs = require('fs');
// const dir   = __dirname + '/data';
// const logf  = 'log.json';
// const path  = dir + '/' + logf;


// //create data directory
// if (!fs.existsSync(dir)) {
//     fs.mkdirSync(dir);
// }

// //create a log file if it does not exist
// fs.open(path, 'wx', (err, fd) => {
// 	if (err) {
// 		if (err.code === "EEXIST") {
// 			console.error('Log file found: ' + path);
// 		  	return;
// 		} else {
// 			throw err;
// 		}
// 		console.error('Log file created: ' + path);
// 	}
// });
/****** ONLY FOR FILE END*******/

/************* WEB APP SERVER ****************/
var http = require('http');
var fs = require('fs');

// Loading the index file . html displayed to the client
var webAppserver = http.createServer(function(req, res) {
});

// Loading socket.io
var io = require('socket.io').listen(webAppserver);

// When a client connects, we note it in the console
io.sockets.on('connection', function (socket) {
    console.log('Web app is connected!');
});


webAppserver.listen(8181);
/************* WEB APP SERVER END ****************/

/************* WRITE QUERY ****************/
const insertQuery = 'INSERT INTO gps_entry_test SET ?';

/*************** CONNECT DB ******************/
var db = mysql.createConnection({
  host     : 'localhost'
});

/************* SERVER START ****************/

//create and bind via TCP
const server = net.createServer( (cSock) => {
	console.log('Client Connected');
	cSock.on('end', () => {
		console.log('Client Disconnected');
	});
  
	//if client sends data, append to log file
	cSock.on('data', function(jsondata) {  
		/*		
		fs.appendFile(path, inBuff, function(err) {
			if(err) {
				return console.log(err);
			}
		}); 
		*/
		/*
		var sendMapData = '';
		sendMapData += jsondata['mac'];
		sendMapData += ';';
		sendMapData += jsondata['username'];
		sendMapData += ';';
		sendMapData += jsondata['longitude'];
		sendMapData += ';';
		sendMapData += jsondata['latitude'];
		io.emit('mapData', sendMapData); // Jamie
		*/

		// connect to database
		db.connect(function(err) {
			if (err) {
				console.error('Error connecting to DB: ' + err.stack);
		    	return;
		  	}
		  	console.log('DB connected.');
		});

		//write data to Db
		db.query(insertQuery, jsondata, function (err, res, fields){
			
			if (err) {
				console.error('Error writing to db: ' + err.stack);
		    	return;
		  	}
		  	console.log('Inserted new entry, id: '+ res.insertId);
		});
		// connect log the query and close connection
		console.log(query.sql);
		db.end(function(err) {
			if (err) {
				console.error('error closing DB: ' + err.stack);
		    	return;
		  	}
		  	console.log('DB closed.');
		});
	});
});

//if server encounters error
server.on('error', (err) => {
	throw err;
});

//listen to socket
server.listen(port, () => {
	console.log('Server listening on port: ' + port);
});

/************* SERVER END ****************/