var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.bnData;
    $scope.dateToUse;
    $scope.writtenDate;

    $scope.makeScreenshot = function() {
        $http.get('/makeScreenshot/');
    }

    $scope.selectDate = function() {
        $http.post('/getFollowersForDate', $scope.dateToUse).success(function(data) {
            $scope.bnData = data;
        })

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
});