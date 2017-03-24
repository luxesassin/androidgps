/*------------------------------------------------------------------------------
-- SOURCE FILE: Server.js - Node Server 
--
-- PROGRAM: COMP4985 A3 
--
-- DATE: Mar. 23, 2016
--
-- REVISIONS: 
-- Version 1.0 - [EY] - 2016/Mar/23 - Created simple server 

-- DESIGNER: Eva Yu
--
-- PROGRAMMER: Eva Yu
--
-- NOTES:
-- Simple node server that will read incoming data from port 7424
-- and store all the data in a json file
------------------------------------------------------------------------------*/
const net = require('net');
const dir   = __dirname + '/data';
const logf  = 'log.json';
const path  = dir + '/' + logf;

var fs = require('fs');
const port = 7424;

//create data directory
if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir);
}

//create a log file if it does not exist
fs.open(path, 'wx', (err, fd) => {
	if (err) {
		if (err.code === "EEXIST") {
			console.error('Log file found: ' + path);
		  	return;
		} else {
			throw err;
		}
		console.error('Log file created: ' + path);
	}
});

/************* SERVER START ****************/

//create and bind via TCP
const server = net.createServer( (cSock) => {
	console.log('Client Connected');
	cSock.on('end', () => {
		console.log('Client Disconnected');
	});
  
	//if client sends data, append to log file
	cSock.on('data', function(inBuff) {  
		fs.appendFile(path, inBuff, function(err) {
			if(err) {
				return console.log(err);
			}
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