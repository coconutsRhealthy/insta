var mainApp = angular.module("mainApp", []);

mainApp.controller('buzzwordsController', function($scope, $http) {

    $scope.bnData;

    $http.get('/getFollowerGrowth').success(function(data) {
        $scope.bnData = data;
    })
});