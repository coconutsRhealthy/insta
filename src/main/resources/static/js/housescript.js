var mainApp = angular.module("mainApp", []);

mainApp.controller('houseController', function($scope, $http) {

    $scope.postCodeToUse;
    $scope.radioButtonValue = "12months";

    $scope.dataToSendToServer = [];

    $scope.makelaar;
    $scope.avPrice;
    $scope.avPriceM2;
    $scope.city;
    $scope.numberOfHouses;
    $scope.showPostCodeData = false;
    $scope.disableGetInfoButton = true;

    $scope.getPostCodeData = function() {
        $scope.dataToSendToServer[0] = $scope.postCodeToUse;
        $scope.dataToSendToServer[1] = $scope.radioButtonValue;

        $http.post('/getPostCodeInfo', $scope.dataToSendToServer).success(function(data) {
            $scope.makelaar = data.mostUsedMakelaar;
            $scope.avPrice = data.averageHousePrice;
            $scope.avPriceM2 = data.averageHousePricePerM2;
            $scope.city = data.city;
            $scope.numberOfHouses = data.numberOfHousesSold;
            $scope.showPostCodeData = true;
        })
    }
});