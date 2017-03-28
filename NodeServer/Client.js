/*------------------------------------------------------------------------------
-- SOURCE FILE: Client.js - Node Client 
--
-- NOTES:
-- Test Client to run against the server! 
------------------------------------------------------------------------------*/
const net = require('net');
const port = 7424;
var jsontestdata = "{'mac'='00:25:96:FF:FE:12:34:01','username' = 'eva-test','longitude' = 49.249914, 'latitude' = -122.985282 }";
const client = net.connect({port: port}, () => {
  // 'connect' listener
  console.log('connected to server!');
  client.write(jsontestdata);
  client.end();
});

client.on('end', () => {
  console.log('disconnected from server');
});