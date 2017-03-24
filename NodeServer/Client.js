/*------------------------------------------------------------------------------
-- SOURCE FILE: Client.js - Node Client 
--
-- NOTES:
-- Test Client to run against the server! 
------------------------------------------------------------------------------*/
const net = require('net');
const port = 7424;
const client = net.connect({port: port}, () => {
  // 'connect' listener
  console.log('connected to server!');
  client.write('Hello world!\r\n');
  client.end();
});

client.on('end', () => {
  console.log('disconnected from server');
});