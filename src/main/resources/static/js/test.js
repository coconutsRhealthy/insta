var mainApp = angular.module("mainApp", []);

mainApp.controller('ContactListCtrl', function ($scope, $timeout, $filter) {

    $scope.alleBuurten = [
        {
            postcode: 1071,
            plaats: "Amsterdam",
            prijs: 1412684.0,
            prijs_m2: 8599.0,
            aantal: 165,
        },
        {
            postcode: 2111,
            plaats: "Aerdenhout",
            prijs: 1174357.0,
            prijs_m2: 5804.0,
            aantal: 62,
        },
        {
            postcode: 2243,
            plaats: "Wassenaar",
            prijs: 1202605.0,
            prijs_m2: 5415.0,
            aantal: 76,
        },
    ];

    $scope.alleBuurten = $filter('orderBy')($scope.alleBuurten, "-prijs_m2", true);

    //var sortingOrder = 'aantal';
    $scope.sortingOrder = 'aantal';

    $scope.setLoading = function (loading) {
        $scope.isLoading = loading;
    }

    $scope.layoutDone = function (value) {
        console.log(value);
        $scope.setLoading(true);

        $timeout(function() {
            if ($scope.sortingOrder !== '') {
                if(value === 'aantal') {

                    $scope.alleBuurten = $filter('orderBy')($scope.alleBuurten, $scope.sortingOrder, false);
                } else if(value === '-aantal') {

                    $scope.alleBuurten = $filter('orderBy')($scope.alleBuurten, $scope.sortingOrder, true);
                } else if(value === 'prijs') {

                    $scope.alleBuurten = $filter('orderBy')($scope.alleBuurten, $scope.sortingOrder, true);
                }
            }

            $scope.setLoading(false);
        }, 1000);
    }

    $scope.loadFeed = function(url) {
	  $scope.setLoading(true);
	}

    $scope.loadFeed();
});

mainApp.directive('repeatDone', function() {
    return function(scope, element, attrs) {
        if (scope.$last) { // all are rendered
            scope.$eval(attrs.repeatDone);
        }
    }
})
