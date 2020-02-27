var mainApp = angular.module("mainApp", []);

mainApp.controller('ContactListCtrl', function ($scope, $timeout, $filter) {

    var sortingOrder = 'name';
    $scope.sortingOrder = sortingOrder;



    $scope.sortorder = 'surname';
    $scope.contacts = [{
        "name": "Richard",
        "surname": "Stallman",
        "telephone": "1234 98765"
    }, {
        "name": "Donald",
        "surname": "Knuth",
        "telephone": "3456 76543"
    }, {
        "name": "Linus",
        "surname": "Torvalds",
        "telephone": "2345 87654"
    }];

    $scope.setLoading = function (loading) {
        $scope.isLoading = loading;
    }

    $scope.layoutDone = function (value) {
        console.log(value);
        $scope.setLoading(true);

        $timeout(function() {
        // take care of the sorting order
        if ($scope.sortingOrder !== '') {

            if(value == 'surname'){
            $scope.contacts = $filter('orderBy')($scope.contacts, $scope.sortingOrder, false);
            }
            else if(value == '-surname'){
            $scope.contacts = $filter('orderBy')($scope.contacts, $scope.sortingOrder, true);
            }
            else{
              $scope.contacts = $filter('orderBy')($scope.contacts, $scope.sortingOrder, false);
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
