var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {
    $scope.startGame = function() {
        alert("hoihoi");
        $http.get('/startGame').success(function(data) {
            alert("heeee");
        })
    }
});