/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 62.875, "KoPercent": 37.125};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.598125, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.01, 500, 1500, "SignUp"], "isController": false}, {"data": [0.01, 500, 1500, "Add Inventory"], "isController": false}, {"data": [1.0, 500, 1500, "Search Flight"], "isController": false}, {"data": [1.0, 500, 1500, "Cancel ticket"], "isController": false}, {"data": [1.0, 500, 1500, "Get Ticket history by email"], "isController": false}, {"data": [0.755, 500, 1500, "Login"], "isController": false}, {"data": [1.0, 500, 1500, "Get Ticket Details by PNR"], "isController": false}, {"data": [0.01, 500, 1500, "Book Ticket"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 800, 297, 37.125, 111.71874999999999, 4, 999, 29.0, 449.79999999999995, 531.9499999999999, 800.2600000000007, 401.60642570281124, 2380.8623164533133, 176.48719879518072], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["SignUp", 100, 99, 99.0, 286.44000000000005, 14, 866, 231.5, 479.6, 494.84999999999997, 865.6199999999998, 60.24096385542169, 21.06080572289157, 18.82530120481928], "isController": false}, {"data": ["Add Inventory", 100, 99, 99.0, 25.089999999999993, 7, 68, 21.0, 47.70000000000002, 52.94999999999999, 67.99, 59.701492537313435, 11.848180970149253, 33.057369402985074], "isController": false}, {"data": ["Search Flight", 100, 0, 0.0, 15.360000000000003, 4, 57, 11.0, 33.400000000000034, 42.849999999999966, 56.969999999999985, 61.34969325153374, 160.13767733895708, 28.038726993865033], "isController": false}, {"data": ["Cancel ticket", 100, 0, 0.0, 36.83000000000001, 11, 104, 33.0, 60.30000000000004, 84.29999999999984, 103.96999999999998, 63.775510204081634, 3.9859693877551017, 23.66669323979592], "isController": false}, {"data": ["Get Ticket history by email", 100, 0, 0.0, 18.709999999999997, 5, 50, 18.0, 31.0, 33.94999999999999, 49.929999999999964, 63.051702395964696, 2708.560130635246, 22.65920554854981], "isController": false}, {"data": ["Login", 100, 0, 0.0, 449.2100000000001, 108, 999, 496.5, 633.4000000000001, 857.0499999999993, 998.8699999999999, 56.21135469364812, 33.101022344013494, 17.181791034288928], "isController": false}, {"data": ["Get Ticket Details by PNR", 100, 0, 0.0, 19.609999999999996, 6, 67, 17.0, 34.80000000000001, 44.89999999999998, 66.87999999999994, 62.46096189881324, 32.28084985946283, 21.348961586508434], "isController": false}, {"data": ["Book Ticket", 100, 99, 99.0, 42.499999999999986, 14, 156, 40.0, 61.0, 75.64999999999992, 155.46999999999974, 61.19951040391677, 8.722125535495717, 49.84413249694003], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["400/Bad Request", 90, 30.303030303030305, 11.25], "isController": false}, {"data": ["500/Internal Server Error", 4, 1.3468013468013469, 0.5], "isController": false}, {"data": ["401/Unauthorized", 9, 3.0303030303030303, 1.125], "isController": false}, {"data": ["409/Conflict", 194, 65.31986531986531, 24.25], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 800, 297, "409/Conflict", 194, "400/Bad Request", 90, "401/Unauthorized", 9, "500/Internal Server Error", 4, "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["SignUp", 100, 99, "400/Bad Request", 90, "401/Unauthorized", 9, "", "", "", "", "", ""], "isController": false}, {"data": ["Add Inventory", 100, 99, "409/Conflict", 95, "500/Internal Server Error", 4, "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["Book Ticket", 100, 99, "409/Conflict", 99, "", "", "", "", "", "", "", ""], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
