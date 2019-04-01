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
    }
});