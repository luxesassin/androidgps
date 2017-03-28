var dataManger = new Map(); // holds all locations.
var pinColors = ['http://maps.google.com/mapfiles/ms/icons/pink-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/purple-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/red-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/blue-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/yellow-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/green-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/ltblue-dot.png'
,'http://maps.google.com/mapfiles/ms/icons/orange-dot.png']; // all pin colours.

/**
  Author: Jamie Lee.
  Date: 2017-Mar-20.
  Prototype: ffunction initMap()
  Arguments: None.
  Purpose: Show all locations on the google map.
  */
  var pinColorIndex = 0;
  var myOptions;
  var map;
  function initMap() {
    var latlng = new google.maps.LatLng(49.254908, -122.913864);
    myOptions = {
      zoom: 10,
      center: latlng,
      mapTypeControl: true,
      mapTypeControlOptions: { style: google.maps.MapTypeControlStyle.DROPDOWN_MENU },
      navigationControl: true,
      mapTypeId: google.maps.MapTypeId.ROADMAP
    };

    map = new google.maps.Map(document.getElementById('map'), myOptions);
    dataManger.forEach(function (value, key, mapObj) {
      var clientId = key;
      value.forEach(function (value, key, mapObj) {
        showLocation(clientId, Number(value.get('lat')), Number(value.get('lgn')), value.get('title'));
        /*
        var infowindow = new google.maps.InfoWindow({
          content: '<b>' + value.get('title') + '</b>',
          size: new google.maps.Size(150, 50)
        });

        var pos = new google.maps.LatLng(Number(value.get('lat')), Number(value.get('lgn')));

        var marker = new google.maps.Marker({
          position: pos,
          map: map,
          title: value.get('title'),
          icon: pinColors[pinColorIndex % pinColors.length]
        });

        google.maps.event.addListener(marker, 'mouseover', function () {
          infowindow.open(map, marker);
        });
        google.maps.event.addListener(marker, 'mouseout', function () {
          infowindow.close(map, marker);
        });
        google.maps.event.addListener(marker, 'click', function () {
        // TODO: Do something when the marker is clicked.
        alert('clicked: position-' + marker.position + ', title-' + marker.title);
      });
*/
      });
      pinColorIndex++;
    });
  }

var pinMap = new Map();
var pinIndex = -1;
function showLocation(clientId, lat, lng, title) {
  var pin = pinMap.get(clientId);
    if (pin == null) {
      pinIndex = ++pinIndex % pinColors.length;
      pinMap.set(clientId, pinColors[pinIndex]);
      pin = pinColors[pinIndex];
    }

    var infowindow = new google.maps.InfoWindow({
      content: '<b>' + title + '</b>',
      size: new google.maps.Size(150, 50)
    });

    var pos = new google.maps.LatLng(lat, lng);

    var marker = new google.maps.Marker({
      position: pos,
      map: map,
      title: title,
      icon: pin
    });

    google.maps.event.addListener(marker, 'mouseover', function () {
      infowindow.open(map, marker);
    });
    google.maps.event.addListener(marker, 'mouseout', function () {
      infowindow.close(map, marker);
    });
    google.maps.event.addListener(marker, 'click', function () {
    // TODO: Do something when the marker is clicked.
    alert('clicked: position-' + marker.position + ', title-' + marker.title);
  });

  console.log(clientId);
  var clientDataMap = dataManger.get(clientId);
  var coords = [];
  clientDataMap.forEach(function (value, key, mapObj) {
    var latlng = new google.maps.LatLng(Number(value.get('lat')), Number(value.get('lgn')));
    coords.push({lat: Number(value.get('lat')), lng: Number(value.get('lgn'))});
  });
  console.log(coords.length);

  var path = new google.maps.Polyline({
          path: coords,
          geodesic: true,
          strokeColor: '#FF0000',
          strokeOpacity: 1.0,
          strokeWeight: 2
        });

  path.setMap(map);
}

/**
  Author: Jamie Lee.
  Date: 2017-Mar-20.
  Prototype: function addMap(client, id, lat, lgn, title)
  Arguments:
              client - The ID of a client.
              id     - The Id of the client's data.
              lat    - The latitude of the data.
              lgn    - The longitude of the data.
              title  - The title for a marker.
  Purpose: Add one location data for a client that has 'client' ID.
  */
  function addMap(client, id, lat, lgn, title) {
    var mapData = new Map();
    mapData.set('lat', lat);
    mapData.set('lgn', lgn);
    mapData.set('title', title);

    var clientDataMap = dataManger.get(client);
    if (dataManger.get(client) == null) {
      clientDataMap = new Map();
    }

    clientDataMap.set(id, mapData);
    dataManger.set(client, clientDataMap);
  }