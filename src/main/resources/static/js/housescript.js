var mainApp = angular.module("mainApp", []);

mainApp.controller('houseController', function($scope, $http) {

    $scope.postCodeToUse;

    $scope.makelaar;
    $scope.avPrice;
    $scope.avPriceM2;
    $scope.city;
    $scope.numberOfHouses;

    $scope.getPostCodeData = function() {
        $http.post('/getPostCodeInfo', $scope.postCodeToUse).success(function(data) {
            $scope.makelaar = data.mostUsedMakelaar;
            $scope.avPrice = data.averageHousePrice;
            $scope.avPriceM2 = data.averageHousePricePerM2;
            $scope.city = data.city;
            $scope.numberOfHouses = data.numberOfHousesSold;
        })
    }
});