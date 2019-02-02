$(document).ready(() => {
    let cursorString = "";
    charts = new Object();
    window.setInterval(() => {
        $.ajax({
            url: "/getData" + (cursorString != "" ?("?cursorString=" + cursorString) : ""),
            success: (receivedData) => {
                populateData(receivedData.data.reverse());
                cursorString = receivedData.cursorString ? receivedData.cursorString : cursorString;
            }
        });
    }, 500);
    let populateData = (data) => {
        var dataMap = new Map([['temperature',[]], ['humidity',[]], ['pH',[]], ['conductivity',[]], ['timestamp',[]]]);
        console.log(data);
        data.forEach((sensorData) => {
            dataMap.get('temperature').push(sensorData['temperature']);
            dataMap.get('humidity').push(sensorData['humidity']);
            dataMap.get('pH').push(sensorData['pH']);
            dataMap.get('conductivity').push(sensorData['conductivity']);
            dataMap.get('timestamp').push(sensorData['timestamp']);
        });
        ['temperature', 'humidity', 'pH', 'conductivity'].forEach((type) => {
            var dataToPopulate = dataMap.get(type);
            var timestamp = dataMap.get('timestamp');
            cursorString == "" || $.isEmptyObject(charts) ? drawGraph(type, dataToPopulate, timestamp) : reDrawGraph(type, dataToPopulate, timestamp);
        });
    }
    let drawGraph = (type, data, timestamp) => {
        var normalizedData = data;
        var ctx = document.getElementById(type + 'Chart').getContext('2d');
        charts[type + 'Chart'] = new Chart(ctx, {
            type: 'line',
            data: {
                labels: timestamp.map(milliseconds => convertToTimeString(milliseconds)),
                datasets: [{
                    label: type.charAt(0).toUpperCase() + type.slice(1),
                    data: normalizedData,
                    borderWidth: 3
                }]
            },
            options: {
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    }
    let reDrawGraph = (type, data, timestamp) => {
        if(data.length == 0) return false;
        var normalizedData = data;
        var charObject = charts[type + 'Chart'];
        var sizeOfData = charObject.data.datasets[0].data.length;
        var dataToBeModified = charObject.data.datasets[0].data;
        var timestampDataToBeModified = charObject.data.labels;
        if(dataToBeModified.length > 10){
            dataToBeModified = dataToBeModified.slice(data.length);
            timestampDataToBeModified = timestampDataToBeModified.slice(timestamp.length);
        } else if(dataToBeModified.length + data.length > 10) {
            var newSize = dataToBeModified.length + data.length;
            dataToBeModified = dataToBeModified.slice(newSize - 10);
            timestampDataToBeModified = timestampDataToBeModified.slice(newSize - 10);
        }
        charObject.data.datasets[0].data = dataToBeModified.concat(data);
        charObject.data.labels = timestampDataToBeModified.concat(timestamp.map(milliseconds => convertToTimeString(milliseconds)));
        charObject.update();
    }
    let convertToTimeString = (milliseconds) => {
        var date = new Date(milliseconds);
        return (date.getHours() > 12 ? (date.getHours() - 12) : date.getHours()) + ":" + date.getMinutes() + ":" + date.getSeconds() + " " + (date.getHours() > 12 ? "PM" : "AM");
    }
});