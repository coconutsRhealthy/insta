var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.bnData;
    $scope.dateToUse;
    $scope.writtenDate;
    $scope.reverseOrder = false;

    //simpleHistogramPrice();
    //simpleHistogramM2();
    doubleHistogram();
    doubleHistogramM2();

    $scope.makeScreenshot = function() {
        $http.get('/makeScreenshot/');
    }

    $scope.selectDate = function() {
        $scope.reverseOrder = false;

        $http.post('/getFollowersForDate', $scope.dateToUse).success(function(data) {
            $scope.bnData = data;
        })

        setWrittenDate();
    }

    $scope.selectDateBottom = function() {
        $scope.dateToUse = $scope.dateToUse + "Reverse";
        $scope.reverseOrder = true;

        $http.post('/getFollowersForDate', $scope.dateToUse).success(function(data) {
            $scope.bnData = data;
        })

        $scope.dateToUse = $scope.dateToUse.replace("Reverse", "");

        setWrittenDate();
    }

    function setWrittenDate() {
        var splittedDate = $scope.dateToUse.split("-");

        var dayToUse = splittedDate[2];
        var monthToUse = splittedDate[1];
        var yearToUse = splittedDate[0];

        if(dayToUse.includes("0")) {
            dayToUse = dayToUse.replace("0", "");
        }

        switch(monthToUse) {
            case '01':
                monthToUse = "januari";
                break;
            case '02':
                monthToUse = "februari";
                break;
            case '03':
                monthToUse = "maart";
                break;
            case '04':
                monthToUse = "april";
                break;
            case '05':
                monthToUse = "mei";
                break;
            case '06':
                monthToUse = "juni";
                break;
            case '07':
                monthToUse = "juli";
                break;
            case '08':
                monthToUse = "augustus";
                break;
            case '09':
                monthToUse = "september";
                break;
            case '10':
                monthToUse = "oktober";
                break;
            case '11':
                monthToUse = "november";
                break;
            case '12':
                monthToUse = "december";
                break;
        }

        $scope.writtenDate = dayToUse + " " + monthToUse + " " + yearToUse;
    }

    function simpleHistogramPrice() {
       var x = [];

       $http.get('/simpleHistogramPrice/').success(function(data) {
          x = data;

          var trace = {
             x: x,
             type: 'histogram',
          };

          var plotlyData = [trace];
          var layout = {
            title: {
              text:'Amsterdam House Prices',
              font: {
                family: 'Courier New, monospace',
                size: 24
              },
              xref: 'paper',
              x: 0.05,
            },
            xaxis: {
              title: {
                text: 'Price',
                font: {
                  family: 'Courier New, monospace',
                  size: 18,
                  color: '#7f7f7f'
                }
              },
            },
            yaxis: {
              title: {
                text: 'Occurrences count ',
                font: {
                  family: 'Courier New, monospace',
                  size: 18,
                  color: '#7f7f7f'
                }
              }
            }
          }
          Plotly.newPlot('myDiv1', plotlyData, layout, {responsive: true});
       })
    }

    function simpleHistogramM2() {
       var x = [];

       $http.get('/simpleHistogramM2/').success(function(data) {
          x = data;

          var trace = {
             x: x,
             type: 'histogram',
          };

          var plotlyData = [trace];
          var layout = {
            title: {
              text:'Amsterdam House Prices per m2',
              font: {
                family: 'Courier New, monospace',
                size: 24
              },
              xref: 'paper',
              x: 0.05,
            },
            xaxis: {
              title: {
                text: 'Price per m2',
                font: {
                  family: 'Courier New, monospace',
                  size: 18,
                  color: '#7f7f7f'
                }
              },
            },
            yaxis: {
              title: {
                text: 'Cccurrences count',
                font: {
                  family: 'Courier New, monospace',
                  size: 18,
                  color: '#7f7f7f'
                }
              }
            }
          }
          Plotly.newPlot('myDiv2', plotlyData, layout, {responsive: true});
       })
    }

    function doubleHistogram() {
        var x1 = [];
        var x2 = [];

        $http.get('/doubleHistogram/').success(function(data) {
            x1 = data[0];
            x2 = data[1];

            var trace1 = {
              x: x1,
              type: "histogram",
              histnorm: 'probability',
              name: 'Zwolle',
              opacity: 0.5,
              marker: {
                 color: 'green',
              },
            };

            var trace2 = {
              x: x2,
              type: "histogram",
              histnorm: 'probability',
              name: 'Amsterdam',
              opacity: 0.6,
              marker: {
                 color: 'red',
              },
            };

            var plotlyData = [trace1, trace2];

            var layout = {
              barmode: "overlay",
              title: {
                text:'House Prices',
                font: {
                  family: 'Courier New, monospace',
                  size: 24
                },
                xref: 'paper',
                x: 0.05,
              },
              xaxis: {
                title: {
                  text: '€ Price',
                  font: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#7f7f7f'
                  }
                },
              },
              yaxis: {
                tickformat: ',.0%',
                title: {
                  text: 'Occurrences count',
                  font: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#7f7f7f'
                  }
                }
              }
            }
            Plotly.newPlot('myDiv1', plotlyData, layout, {responsive: true});
        })
    }

    function doubleHistogramM2() {
        var x1 = [];
        var x2 = [];

        $http.get('/doubleHistogramM2/').success(function(data) {
            x1 = data[0];
            x2 = data[1];

            var trace1 = {
              x: x1,
              type: "histogram",
              histnorm: 'probability',
              name: 'Zwolle',
              opacity: 0.5,
              marker: {
                 color: 'green',
              },
            };

            var trace2 = {
              x: x2,
              type: "histogram",
              histnorm: 'probability',
              name: 'Amsterdam',
              opacity: 0.6,
              marker: {
                 color: 'red',
              },
            };

            var plotlyData = [trace1, trace2];

            var layout = {
              barmode: "overlay",
              title: {
                text:'House Prices per m2',
                font: {
                  family: 'Courier New, monospace',
                  size: 24
                },
                xref: 'paper',
                x: 0.05,
              },
              xaxis: {
                title: {
                  text: '€ Price per m2',
                  font: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#7f7f7f'
                  }
                },
              },
              yaxis: {
                tickformat: ',.0%',
                title: {
                  text: 'Occurrences count',
                  font: {
                    family: 'Courier New, monospace',
                    size: 18,
                    color: '#7f7f7f'
                  }
                }
              }
            }
            Plotly.newPlot('myDiv2', plotlyData, layout, {responsive: true});
        })
    }
});