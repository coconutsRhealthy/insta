var mainApp = angular.module("mainApp", []);

mainApp.controller('houseController', function($scope, $http) {

    $scope.postCodeToUse;

    $scope.getPostCodeData = function() {
        alert("beestje")

        $http.post('/getPostCodeInfo', $scope.postCodeToUse).success(function(data) {
            alert("JUP!..!");
        })
    }
});