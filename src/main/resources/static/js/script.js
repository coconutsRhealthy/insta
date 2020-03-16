var mainApp = angular.module("mainApp", []);

mainApp.controller('ContactListCtrl', function ($scope, $timeout, $filter) {

    var upArrow = "\u25B2";
    var downArrow = "\u25BC";
    $scope.price_arrow = downArrow;
    $scope.price_m2_arrow = "";
    $scope.amount_arrow = "";

    $scope.orderType = "-prijs_12m";
    $scope.initialPostCodesOrCities = "postcodes";
    $scope.postCodesOrCitiesPeriod = "postcodesInitial12months";
    $scope.tableHead = "postcodes";
    $scope.initialPeriod = "12months";
    $scope.period = "12months";

    $scope.setLoading = function (loading) {
       $scope.isLoading = loading;
    }

    $scope.convertToCurrency = function(value) {
       var formatter = new Intl.NumberFormat('nl-NL', {
         style: 'currency',
         currency: 'EUR',
       });

       var toReturn = formatter.format(value);
       toReturn = toReturn.substring(0, toReturn.indexOf(","));
       return toReturn;
    }

    $scope.changeList = function(value) {
       $timeout(function() {
            if(value === "cities") {
                $scope.tableHead = "cities";
            }

            if(value === "postcodes") {
                $scope.tableHead = "postcodes";
            }

            if(value.includes("months")) {
                $scope.period = value;
            }

            setPostCodesOrCitiesPeriod(value);

            $scope.price_arrow = downArrow;
            $scope.price_m2_arrow = "";
            $scope.amount_arrow = "";
       }, 30);
    }

    $scope.changePeriod = function(value) {
       $scope.setLoading(true);

       $timeout(function() {
            $scope.period = value;
       }, 30);

      $timeout(function() {
          $scope.setLoading(false);
      }, 30);
    }

    function setPostCodesOrCitiesPeriod(input) {
        if(input === "postcodes") {
            if($scope.postCodesOrCitiesPeriod.includes("Initial")) {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("cities", "postcodes");
            } else {
                if(!$scope.postCodesOrCitiesPeriod.includes("months")) {
                    $scope.postCodesOrCitiesPeriod = input + "Initial" + $scope.period;
                } else {
                    $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("cities", "postcodesInitial");
                }
            }
        } else if(input === "cities") {
            if($scope.postCodesOrCitiesPeriod.includes("Initial")) {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("postcodes", "cities");
            } else {
                if(!$scope.postCodesOrCitiesPeriod.includes("months")) {
                    $scope.postCodesOrCitiesPeriod = input + "Initial" + $scope.period;
                } else {
                    $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("postcodes", "citiesInitial");
                }
            }
        } else if(input === "6months") {
            if($scope.postCodesOrCitiesPeriod.includes("12months")) {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("12months", "6months");
            } else {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod + "Initial" + input;
            }
        } else if(input === "12months") {
            if($scope.postCodesOrCitiesPeriod.includes("6months")) {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod.replace("6months", "12months");
            } else {
                $scope.postCodesOrCitiesPeriod = $scope.postCodesOrCitiesPeriod + "Initial" + input;
            }
        }
    }

    $scope.doSorting = function (type) {
       $scope.setLoading(true);

       var periodAddition = getPeriodAddition();

       $timeout(function() {
            var orderTypeCheck = $scope.orderType.replace("-", "");

            orderTypeCheck = orderTypeCheck.replace("_6m", "");
            orderTypeCheck = orderTypeCheck.replace("_12m", "");

            if(orderTypeCheck !== type) {
                if(type === "prijs") {
                    $scope.orderType = "-prijs" + periodAddition;
                    sortIndividualTable();
                    $scope.price_arrow = downArrow;
                    $scope.price_m2_arrow = "";
                    $scope.amount_arrow = "";
                } else if(type === "prijs_m2") {
                    $scope.orderType = "-prijs_m2" + periodAddition;
                    sortIndividualTable();
                    $scope.price_arrow = "";
                    $scope.price_m2_arrow = downArrow;
                    $scope.amount_arrow = "";
                } else if(type === "aantal") {
                    $scope.orderType = "-aantal" + periodAddition;
                    sortIndividualTable();
                    $scope.price_arrow = "";
                    $scope.price_m2_arrow = "";
                    $scope.amount_arrow = downArrow;
                }
            } else {
                if($scope.orderType.indexOf("-") === -1) {
                    $scope.orderType = "-" + $scope.orderType;

                    if($scope.price_arrow !== "") {
                        $scope.price_arrow = downArrow;
                        sortIndividualTable();
                    } else if($scope.price_m2_arrow !== "") {
                        $scope.price_m2_arrow = downArrow;
                        sortIndividualTable();
                    } else {
                        $scope.amount_arrow = downArrow;
                        sortIndividualTable();
                    }
                } else {
                    $scope.orderType = $scope.orderType.replace('-', '');

                    if($scope.price_arrow !== "") {
                        $scope.price_arrow = upArrow;
                        sortIndividualTable();
                    } else if($scope.price_m2_arrow !== "") {
                        $scope.price_m2_arrow = upArrow;
                        sortIndividualTable();
                    } else {
                        $scope.amount_arrow = upArrow;
                        sortIndividualTable();
                    }
                }
            }
       }, 500);

       $timeout(function() {
          $scope.setLoading(false);

       }, 500);
    }

    function sortIndividualTable() {
        if($scope.postCodesOrCitiesPeriod === "postcodesInitial6months") {
            $scope.alleBuurtenShortlist6months = $filter('orderBy')($scope.alleBuurtenShortlist6months, $scope.orderType, false);
        } else if($scope.postCodesOrCitiesPeriod === "postcodesInitial12months") {
            $scope.alleBuurtenShortlist12months = $filter('orderBy')($scope.alleBuurtenShortlist12months, $scope.orderType, false);
        } else if($scope.postCodesOrCitiesPeriod === "citiesInitial6months") {
            $scope.alleWoonplaatsenShortlist6months = $filter('orderBy')($scope.alleWoonplaatsenShortlist6months, $scope.orderType, false);
        } else if($scope.postCodesOrCitiesPeriod === "citiesInitial12months") {
            $scope.alleWoonplaatsenShortlist12months = $filter('orderBy')($scope.alleWoonplaatsenShortlist12months, $scope.orderType, false);
        } else if($scope.postCodesOrCitiesPeriod === "postcodes") {
             $scope.alleBuurtenTotallist = $filter('orderBy')( $scope.alleBuurtenTotallist, $scope.orderType, false);
        } else if($scope.postCodesOrCitiesPeriod === "cities") {
            $scope.alleWoonplaatsenTotallist = $filter('orderBy')($scope.alleWoonplaatsenTotallist, $scope.orderType, false);
        }
    }

    function getPeriodAddition() {
        var periodAddition;

        if($scope.period === "6months") {
            periodAddition = "_6m";
        } else {
            periodAddition = "_12m";
        }

        return periodAddition;
    }

    $scope.showEntireList = function() {
       $scope.setLoading(true);

       $timeout(function() {
            if($scope.postCodesOrCitiesPeriod.includes("postcodes")) {
                $scope.postCodesOrCitiesPeriod = "postcodes";
            } else if($scope.postCodesOrCitiesPeriod.includes("cities")) {
                $scope.postCodesOrCitiesPeriod = "cities";
            }

       }, 30);

       $timeout(function() {
           $scope.setLoading(false);

       }, 30);
    }

    $scope.alleWoonplaatsenShortlist6months = [
        {
            plaats: "woonplaatsShort6month1",
            prijs_6m: 6800000.0,
            prijs_m2_6m: 5945.0,
            aantal_6m: 32,
        },
        {
            plaats: "woonplaatsShort6month2",
            prijs_6m: 6700000.0,
            prijs_m2_6m: 4808.0,
            aantal_6m: 60,
        },
        {
            plaats: "woonplaatsShort6month3",
            prijs_6m: 6600000.0,
            prijs_m2_6m: 3694.0,
            aantal_6m: 13,
        },
        {
            plaats: "woonplaatsShort6month4",
            prijs_6m: 541683.0,
            prijs_m2_6m: 4615.0,
            aantal_6m: 538,
        },
        {
            plaats: "woonplaatsShort6month5",
            prijs_6m: 540666.0,
            prijs_m2_6m: 3098.0,
            aantal_6m: 3,
        },
        {
            plaats: "woonplaatsShort6month6",
            prijs_6m: 513772.0,
            prijs_m2_6m: 5670.0,
            aantal_6m: 5197,
        },
        {
            plaats: "woonplaatsShort6month7",
            prijs_6m: 511000.0,
            prijs_m2_6m: 3180.0,
            aantal_6m: 10,
        },
        {
            plaats: "woonplaatsShort6month8",
            prijs_6m: 503675.0,
            prijs_m2_6m: 2388.0,
            aantal_6m: 4,
        },
        {
            plaats: "woonplaatsShort6month9",
            prijs_6m: 493333.0,
            prijs_m2_6m: 2211.0,
            aantal_6m: 6,
        },
        {
            plaats: "woonplaatsShort6month10",
            prijs_6m: 479250.0,
            prijs_m2_6m: 3643.0,
            aantal_6m: 116,
        },
    ];

    $scope.alleWoonplaatsenShortlist12months = [
        {
            plaats: "woonplaatsShort12month1",
            prijs_12m: 1269066.0,
            prijs_m2_12m: 5945.0,
            aantal_12m: 32,
        },
        {
            plaats: "woonplaatsShort12month2",
            prijs_12m: 707781.0,
            prijs_m2_12m: 4808.0,
            aantal_12m: 60,
        },
        {
            plaats: "woonplaatsShort12month3",
            prijs_12m: 560153.0,
            prijs_m2_12m: 3694.0,
            aantal_12m: 13,
        },
        {
            plaats: "woonplaatsShort12month4",
            prijs_12m: 541683.0,
            prijs_m2_12m: 4615.0,
            aantal_12m: 538,
        },
        {
            plaats: "woonplaatsShort12month5",
            prijs_12m: 540666.0,
            prijs_m2_12m: 3098.0,
            aantal_12m: 3,
        },
        {
            plaats: "woonplaatsShort12month6",
            prijs_12m: 513772.0,
            prijs_m2_12m: 5670.0,
            aantal_12m: 5197,
        },
        {
            plaats: "woonplaatsShort12month7",
            prijs_12m: 511000.0,
            prijs_m2_12m: 3180.0,
            aantal_12m: 10,
        },
        {
            plaats: "woonplaatsShort12month8",
            prijs_12m: 503675.0,
            prijs_m2_12m: 2388.0,
            aantal_12m: 4,
        },
        {
            plaats: "woonplaatsShort12month9",
            prijs_12m: 493333.0,
            prijs_m2_12m: 2211.0,
            aantal_12m: 6,
        },
        {
            plaats: "woonplaatsShort12month10",
            prijs_12m: 479250.0,
            prijs_m2_12m: 3643.0,
            aantal_12m: 116,
        },
    ];

    $scope.alleBuurtenShortlist6months = [
    	{
    		postcode: 1071,
    		plaats: "buurtShort6month1",
    		prijs_6m: 1507855.0,
    		prijs_m2_6m: 8628.0,
    		aantal_6m: 83,
    	},
    	{
    		postcode: 1017,
    		plaats: "buurtShort6month2",
    		prijs_6m: 1029059.0,
    		prijs_m2_6m: 8088.0,
    		aantal_6m: 93,
    	},
    	{
    		postcode: 1077,
    		plaats: "buurtShort6month3",
    		prijs_6m: 966151.0,
    		prijs_m2_6m: 7049.0,
    		aantal_6m: 66,
    	},
    	{
    		postcode: 1182,
    		plaats: "buurtShort6month4",
    		prijs_6m: 948500.0,
    		prijs_m2_6m: 6165.0,
    		aantal_6m: 18,
    	},
    	{
    		postcode: 1016,
    		plaats: "buurtShort6month5",
    		prijs_6m: 932359.0,
    		prijs_m2_6m: 7818.0,
    		aantal_6m: 81,
    	},
    	{
    		postcode: 1028,
    		plaats: "buurtShort6month6",
    		prijs_6m: 885000.0,
    		prijs_m2_6m: 5624.0,
    		aantal_6m: 5,
    	},
    	{
    		postcode: 1012,
    		plaats: "buurtShort6month7",
    		prijs_6m: 845547.0,
    		prijs_m2_6m: 7109.0,
    		aantal_6m: 42,
    	},
    	{
    		postcode: 1075,
    		plaats: "buurtShort6month8",
    		prijs_6m: 821878.0,
    		prijs_m2_6m: 7559.0,
    		aantal_6m: 66,
    	},
    	{
    		postcode: 1014,
    		plaats: "buurtShort6month9",
    		prijs_6m: 821446.0,
    		prijs_m2_6m: 6454.0,
    		aantal_6m: 66,
    	},
    	{
    		postcode: 1081,
    		plaats: "buurtShort6month10",
    		prijs_6m: 755987.0,
    		prijs_m2_6m: 5418.0,
    		aantal_6m: 40,
    	},
    ]

    $scope.alleBuurtenShortlist12months = [
        {
            postcode: 1071,
            plaats: "buurtShort12month1",
            prijs_12m: 1507855.0,
            prijs_m2_12m: 8628.0,
            aantal_12m: 83,
        },
        {
            postcode: 1017,
            plaats: "buurtShort12month2",
            prijs_12m: 1029059.0,
            prijs_m2_12m: 8088.0,
            aantal_12m: 93,
        },
        {
            postcode: 1077,
            plaats: "buurtShort12month3",
            prijs_12m: 966151.0,
            prijs_m2_12m: 7049.0,
            aantal_12m: 66,
        },
        {
            postcode: 1182,
            plaats: "buurtShort12month4",
            prijs_12m: 948500.0,
            prijs_m2_12m: 6165.0,
            aantal_12m: 18,
        },
        {
            postcode: 1016,
            plaats: "buurtShort12month5",
            prijs_12m: 932359.0,
            prijs_m2_12m: 7818.0,
            aantal_12m: 81,
        },
        {
            postcode: 1028,
            plaats: "buurtShort12month6",
            prijs_12m: 885000.0,
            prijs_m2_12m: 5624.0,
            aantal_12m: 5,
        },
        {
            postcode: 1012,
            plaats: "buurtShort12month7",
            prijs_12m: 845547.0,
            prijs_m2_12m: 7109.0,
            aantal_12m: 42,
        },
        {
            postcode: 1075,
            plaats: "buurtShort12month8",
            prijs_12m: 821878.0,
            prijs_m2_12m: 7559.0,
            aantal_12m: 66,
        },
        {
            postcode: 1014,
            plaats: "buurtShort12month9",
            prijs_12m: 821446.0,
            prijs_m2_12m: 6454.0,
            aantal_12m: 66,
        },
        {
            postcode: 1081,
            plaats: "buurtShort12month10",
            prijs_12m: 755987.0,
            prijs_m2_12m: 5418.0,
            aantal_12m: 40,
        },
    ]

    $scope.alleWoonplaatsenTotallist = [
    	{
    		plaats: "Aerdenhout",
    		prijs_6m: 1269066.0,
    		prijs_m2_6m: 5945.0,
    		prijs_12m: 1174357.0,
    		prijs_m2_12m: 5804.0,
    		aantal_6m: 32,
    		aantal_12m: 62,
    	},
    	{
    		plaats: "Abcoude",
    		prijs_6m: 707781.0,
    		prijs_m2_6m: 4808.0,
    		prijs_12m: 696023.0,
    		prijs_m2_12m: 4668.0,
    		aantal_6m: 60,
    		aantal_12m: 104,
    	},
    	{
    		plaats: "Ankeveen",
    		prijs_6m: 560153.0,
    		prijs_m2_6m: 3694.0,
    		prijs_12m: 454260.0,
    		prijs_m2_12m: 3493.0,
    		aantal_6m: 13,
    		aantal_12m: 25,
    	},
    	{
    		plaats: "Amstelveen",
    		prijs_6m: 541683.0,
    		prijs_m2_6m: 4615.0,
    		prijs_12m: 525876.0,
    		prijs_m2_12m: 4526.0,
    		aantal_6m: 538,
    		aantal_12m: 1041,
    	},
    	{
    		plaats: "Acquoy",
    		prijs_6m: 540666.0,
    		prijs_m2_6m: 3098.0,
    		prijs_12m: 522000.0,
    		prijs_m2_12m: 3670.0,
    		aantal_6m: 3,
    		aantal_12m: 7,
    	},
    	{
    		plaats: "Amsterdam",
    		prijs_6m: 513772.0,
    		prijs_m2_6m: 5670.0,
    		prijs_12m: 499363.0,
    		prijs_m2_12m: 5608.0,
    		aantal_6m: 5197,
    		aantal_12m: 10112,
    	},
    	{
    		plaats: "Aarlanderveen",
    		prijs_6m: 511000.0,
    		prijs_m2_6m: 3180.0,
    		prijs_12m: 462157.0,
    		prijs_m2_12m: 2938.0,
    		aantal_6m: 10,
    		aantal_12m: 19,
    	},
    	{
    		plaats: "Anloo",
    		prijs_6m: 503675.0,
    		prijs_m2_6m: 2388.0,
    		prijs_12m: 509940.0,
    		prijs_m2_12m: 2339.0,
    		aantal_6m: 4,
    		aantal_12m: 5,
    	},
    	{
    		plaats: "Altforst",
    		prijs_6m: 493333.0,
    		prijs_m2_6m: 2211.0,
    		prijs_12m: 418888.0,
    		prijs_m2_12m: 2318.0,
    		aantal_6m: 6,
    		aantal_12m: 9,
    	},
    	{
    		plaats: "Aalsmeer",
    		prijs_6m: 479250.0,
    		prijs_m2_6m: 3643.0,
    		prijs_12m: 460722.0,
    		prijs_m2_12m: 3617.0,
    		aantal_6m: 116,
    		aantal_12m: 218,
    	},
    	{
    		plaats: "Amerongen",
    		prijs_6m: 477141.0,
    		prijs_m2_6m: 3209.0,
    		prijs_12m: 430488.0,
    		prijs_m2_12m: 3180.0,
    		aantal_6m: 46,
    		aantal_12m: 86,
    	},
    	{
    		plaats: "Abbega",
    		prijs_6m: 476666.0,
    		prijs_m2_6m: 1747.0,
    		prijs_12m: 476666.0,
    		prijs_m2_12m: 1747.0,
    		aantal_6m: 3,
    		aantal_12m: 3,
    	},
    	{
    		plaats: "Ambt Delden",
    		prijs_6m: 469000.0,
    		prijs_m2_6m: 2867.0,
    		prijs_12m: 466050.0,
    		prijs_m2_12m: 2494.0,
    		aantal_6m: 5,
    		aantal_12m: 10,
    	},
    	{
    		plaats: "Aagtekerke",
    		prijs_6m: 446000.0,
    		prijs_m2_6m: 2765.0,
    		prijs_12m: 375785.0,
    		prijs_m2_12m: 2649.0,
    		aantal_6m: 4,
    		aantal_12m: 7,
    	},
    	{
    		plaats: "Aldtsjerk",
    		prijs_6m: 439666.0,
    		prijs_m2_6m: 2136.0,
    		prijs_12m: 427000.0,
    		prijs_m2_12m: 2142.0,
    		aantal_6m: 3,
    		aantal_12m: 6,
    	},
    	{
    		plaats: "Abbenes",
    		prijs_6m: 397583.0,
    		prijs_m2_6m: 3131.0,
    		prijs_12m: 418708.0,
    		prijs_m2_12m: 3319.0,
    		aantal_6m: 6,
    		aantal_12m: 12,
    	},
    	{
    		plaats: "Albergen",
    		prijs_6m: 392000.0,
    		prijs_m2_6m: 2281.0,
    		prijs_12m: 390625.0,
    		prijs_m2_12m: 2236.0,
    		aantal_6m: 13,
    		aantal_12m: 20,
    	},
    	{
    		plaats: "'s-Gravenzande",
    		prijs_6m: 391242.0,
    		prijs_m2_6m: 3025.0,
    		prijs_12m: 364985.0,
    		prijs_m2_12m: 2983.0,
    		aantal_6m: 258,
    		aantal_12m: 397,
    	},
    	{
    		plaats: "'t Loo Oldebroek",
    		prijs_6m: 384250.0,
    		prijs_m2_6m: 2822.0,
    		prijs_12m: 372200.0,
    		prijs_m2_12m: 2839.0,
    		aantal_6m: 8,
    		aantal_12m: 10,
    	},
    	{
    		plaats: "Alphen (GE)",
    		prijs_6m: 375208.0,
    		prijs_m2_6m: 2098.0,
    		prijs_12m: 344460.0,
    		prijs_m2_12m: 2143.0,
    		aantal_6m: 12,
    		aantal_12m: 25,
    	},
    	{
    		plaats: "'s Gravenmoer",
    		prijs_6m: 374710.0,
    		prijs_m2_6m: 2431.0,
    		prijs_12m: 361816.0,
    		prijs_m2_12m: 2437.0,
    		aantal_6m: 19,
    		aantal_12m: 30,
    	},
    	{
    		plaats: "Ammerstol",
    		prijs_6m: 372400.0,
    		prijs_m2_6m: 2999.0,
    		prijs_12m: 386937.0,
    		prijs_m2_12m: 2891.0,
    		aantal_6m: 10,
    		aantal_12m: 16,
    	},
    	{
    		plaats: "Achterveld (UT)",
    		prijs_6m: 371593.0,
    		prijs_m2_6m: 3119.0,
    		prijs_12m: 397789.0,
    		prijs_m2_12m: 3297.0,
    		aantal_6m: 48,
    		aantal_12m: 57,
    	},
    	{
    		plaats: "Almkerk",
    		prijs_6m: 370272.0,
    		prijs_m2_6m: 2816.0,
    		prijs_12m: 337595.0,
    		prijs_m2_12m: 2786.0,
    		aantal_6m: 11,
    		aantal_12m: 21,
    	},
    	{
    		plaats: "Aarle-Rixtel",
    		prijs_6m: 369907.0,
    		prijs_m2_6m: 2176.0,
    		prijs_12m: 377375.0,
    		prijs_m2_12m: 2282.0,
    		aantal_6m: 27,
    		aantal_12m: 64,
    	},
    	{
    		plaats: "Aalst",
    		prijs_6m: 362250.0,
    		prijs_m2_6m: 2551.0,
    		prijs_12m: 367847.0,
    		prijs_m2_12m: 2444.0,
    		aantal_6m: 14,
    		aantal_12m: 23,
    	},
    	{
    		plaats: "Amersfoort",
    		prijs_6m: 361562.0,
    		prijs_m2_6m: 3151.0,
    		prijs_12m: 362839.0,
    		prijs_m2_12m: 3136.0,
    		aantal_6m: 1059,
    		aantal_12m: 2058,
    	},
    	{
    		plaats: "Ameide",
    		prijs_6m: 361090.0,
    		prijs_m2_6m: 2983.0,
    		prijs_12m: 311482.0,
    		prijs_m2_12m: 2648.0,
    		aantal_6m: 11,
    		aantal_12m: 29,
    	},
    	{
    		plaats: "Alphen (NB)",
    		prijs_6m: 356803.0,
    		prijs_m2_6m: 2382.0,
    		prijs_12m: 339225.0,
    		prijs_m2_12m: 2369.0,
    		aantal_6m: 26,
    		aantal_12m: 55,
    	},
    	{
    		plaats: "Achtmaal",
    		prijs_6m: 356214.0,
    		prijs_m2_6m: 2747.0,
    		prijs_12m: 333884.0,
    		prijs_m2_12m: 2515.0,
    		aantal_6m: 7,
    		aantal_12m: 13,
    	},
    	{
    		plaats: "'s-Graveland",
    		prijs_6m: 354333.0,
    		prijs_m2_6m: 3833.0,
    		prijs_12m: 386982.0,
    		prijs_m2_12m: 3676.0,
    		aantal_6m: 12,
    		aantal_12m: 28,
    	},
    	{
    		plaats: "Ammerzoden",
    		prijs_6m: 353968.0,
    		prijs_m2_6m: 2628.0,
    		prijs_12m: 329464.0,
    		prijs_m2_12m: 2547.0,
    		aantal_6m: 24,
    		aantal_12m: 35,
    	},
    	{
    		plaats: "Nieuw Namen",
    		prijs_6m: 351300.0,
    		prijs_m2_6m: 3028.0,
    		prijs_12m: 289222.0,
    		prijs_m2_12m: 2450.0,
    		aantal_6m: 5,
    		aantal_12m: 9,
    	},
    	{
    		plaats: "Angerlo",
    		prijs_6m: 347166.0,
    		prijs_m2_6m: 2107.0,
    		prijs_12m: 349115.0,
    		prijs_m2_12m: 2137.0,
    		aantal_6m: 9,
    		aantal_12m: 13,
    	},
    	{
    		plaats: "Alphen aan den Rijn",
    		prijs_6m: 341958.0,
    		prijs_m2_6m: 2841.0,
    		prijs_12m: 341294.0,
    		prijs_m2_12m: 2789.0,
    		aantal_6m: 642,
    		aantal_12m: 1297,
    	},
    	{
    		plaats: "Amstelhoek",
    		prijs_6m: 338375.0,
    		prijs_m2_6m: 3293.0,
    		prijs_12m: 374136.0,
    		prijs_m2_12m: 3146.0,
    		aantal_6m: 4,
    		aantal_12m: 11,
    	},
    	{
    		plaats: "Abbenbroek",
    		prijs_6m: 334166.0,
    		prijs_m2_6m: 3114.0,
    		prijs_12m: 351284.0,
    		prijs_m2_12m: 2847.0,
    		aantal_6m: 9,
    		aantal_12m: 19,
    	},
    	{
    		plaats: "Akersloot",
    		prijs_6m: 331270.0,
    		prijs_m2_6m: 3036.0,
    		prijs_12m: 360084.0,
    		prijs_m2_12m: 3045.0,
    		aantal_6m: 24,
    		aantal_12m: 53,
    	},
    	{
    		plaats: "Abbekerk",
    		prijs_6m: 329124.0,
    		prijs_m2_6m: 2405.0,
    		prijs_12m: 318325.0,
    		prijs_m2_12m: 2340.0,
    		aantal_6m: 16,
    		aantal_12m: 23,
    	},
    	{
    		plaats: "'t Haantje",
    		prijs_6m: 327500.0,
    		prijs_m2_6m: 2180.0,
    		prijs_12m: 341500.0,
    		prijs_m2_12m: 2374.0,
    		aantal_6m: 4,
    		aantal_12m: 6,
    	},
    	{
    		plaats: "Afferden (GE)",
    		prijs_6m: 326547.0,
    		prijs_m2_6m: 2309.0,
    		prijs_12m: 334500.0,
    		prijs_m2_12m: 2235.0,
    		aantal_6m: 21,
    		aantal_12m: 27,
    	},
    	{
    		plaats: "'s-Gravendeel",
    		prijs_6m: 326122.0,
    		prijs_m2_6m: 2625.0,
    		prijs_12m: 298867.0,
    		prijs_m2_12m: 2580.0,
    		aantal_6m: 49,
    		aantal_12m: 102,
    	},
    	{
    		plaats: "'s-Heerenhoek",
    		prijs_6m: 323750.0,
    		prijs_m2_6m: 2111.0,
    		prijs_12m: 248233.0,
    		prijs_m2_12m: 1911.0,
    		aantal_6m: 8,
    		aantal_12m: 15,
    	},
    	{
    		plaats: "Andelst",
    		prijs_6m: 317714.0,
    		prijs_m2_6m: 2558.0,
    		prijs_12m: 267634.0,
    		prijs_m2_12m: 2523.0,
    		aantal_6m: 7,
    		aantal_12m: 13,
    	},
    	{
    		plaats: "Zwanenburg",
    		prijs_6m: 316573.0,
    		prijs_m2_6m: 2495.0,
    		prijs_12m: 298646.0,
    		prijs_m2_12m: 2441.0,
    		aantal_6m: 254,
    		aantal_12m: 508,
    	},
    	{
    		plaats: "Afferden (LI)",
    		prijs_6m: 309611.0,
    		prijs_m2_6m: 1781.0,
    		prijs_12m: 272833.0,
    		prijs_m2_12m: 1865.0,
    		aantal_6m: 9,
    		aantal_12m: 21,
    	},
    	{
    		plaats: "Andel",
    		prijs_6m: 309565.0,
    		prijs_m2_6m: 2507.0,
    		prijs_12m: 302058.0,
    		prijs_m2_12m: 2457.0,
    		aantal_6m: 39,
    		aantal_12m: 74,
    	},
    	{
    		plaats: "'s-Heer Arendskerke",
    		prijs_6m: 309500.0,
    		prijs_m2_6m: 2357.0,
    		prijs_12m: 311239.0,
    		prijs_m2_12m: 2208.0,
    		aantal_6m: 14,
    		aantal_12m: 23,
    	},
    	{
    		plaats: "Almere",
    		prijs_6m: 308467.0,
    		prijs_m2_6m: 2601.0,
    		prijs_12m: 299379.0,
    		prijs_m2_12m: 2554.0,
    		aantal_6m: 1631,
    		aantal_12m: 3274,
    	},
    	{
    		plaats: "Appeltern",
    		prijs_6m: 308333.0,
    		prijs_m2_6m: 2347.0,
    		prijs_12m: 380875.0,
    		prijs_m2_12m: 2556.0,
    		aantal_6m: 3,
    		aantal_12m: 4,
    	},
    	{
    		plaats: "Apeldoorn",
    		prijs_6m: 307709.0,
    		prijs_m2_6m: 2586.0,
    		prijs_12m: 304808.0,
    		prijs_m2_12m: 2573.0,
    		aantal_6m: 1132,
    		aantal_12m: 2242,
    	},
    	{
    		plaats: "Alkmaar",
    		prijs_6m: 306771.0,
    		prijs_m2_6m: 2982.0,
    		prijs_12m: 301729.0,
    		prijs_m2_12m: 2883.0,
    		aantal_6m: 687,
    		aantal_12m: 1394,
    	},
    	{
    		plaats: "'t Veld",
    		prijs_6m: 303710.0,
    		prijs_m2_6m: 2652.0,
    		prijs_12m: 300437.0,
    		prijs_m2_12m: 2637.0,
    		aantal_6m: 19,
    		aantal_12m: 32,
    	},
    	{
    		plaats: "Angeren",
    		prijs_6m: 303400.0,
    		prijs_m2_6m: 2293.0,
    		prijs_12m: 312625.0,
    		prijs_m2_12m: 2348.0,
    		aantal_6m: 10,
    		aantal_12m: 36,
    	},
    	{
    		plaats: "Anderen",
    		prijs_6m: 297316.0,
    		prijs_m2_6m: 2184.0,
    		prijs_12m: 312533.0,
    		prijs_m2_12m: 2152.0,
    		aantal_6m: 30,
    		aantal_12m: 60,
    	},
    	{
    		plaats: "Alteveer (Gem. De Wolden)",
    		prijs_6m: 293375.0,
    		prijs_m2_6m: 2187.0,
    		prijs_12m: 274125.0,
    		prijs_m2_12m: 2148.0,
    		aantal_6m: 4,
    		aantal_12m: 8,
    	},
    	{
    		plaats: "Annerveenschekanaal",
    		prijs_6m: 289090.0,
    		prijs_m2_6m: 2413.0,
    		prijs_12m: 282408.0,
    		prijs_m2_12m: 2244.0,
    		aantal_6m: 5,
    		aantal_12m: 6,
    	},
    	{
    		plaats: "Alblasserdam",
    		prijs_6m: 288625.0,
    		prijs_m2_6m: 2610.0,
    		prijs_12m: 319687.0,
    		prijs_m2_12m: 2829.0,
    		aantal_6m: 108,
    		aantal_12m: 243,
    	},
    	{
    		plaats: "Annen",
    		prijs_6m: 287711.0,
    		prijs_m2_6m: 2232.0,
    		prijs_12m: 268360.0,
    		prijs_m2_12m: 2243.0,
    		aantal_6m: 26,
    		aantal_12m: 61,
    	},
    	{
    		plaats: "Amstenrade",
    		prijs_6m: 281300.0,
    		prijs_m2_6m: 1841.0,
    		prijs_12m: 260418.0,
    		prijs_m2_12m: 1848.0,
    		aantal_6m: 13,
    		aantal_12m: 32,
    	},
    	{
    		plaats: "Aalden",
    		prijs_6m: 279830.0,
    		prijs_m2_6m: 1801.0,
    		prijs_12m: 266572.0,
    		prijs_m2_12m: 1919.0,
    		aantal_6m: 15,
    		aantal_12m: 27,
    	},
    	{
    		plaats: "Alem",
    		prijs_6m: 279269.0,
    		prijs_m2_6m: 2498.0,
    		prijs_12m: 306736.0,
    		prijs_m2_12m: 2611.0,
    		aantal_6m: 13,
    		aantal_12m: 19,
    	},
    	{
    		plaats: "Zaandijk",
    		prijs_6m: 279173.0,
    		prijs_m2_6m: 2585.0,
    		prijs_12m: 271940.0,
    		prijs_m2_12m: 2553.0,
    		aantal_6m: 101,
    		aantal_12m: 194,
    	},
    	{
    		plaats: "Akkrum",
    		prijs_6m: 279071.0,
    		prijs_m2_6m: 2220.0,
    		prijs_12m: 274025.0,
    		prijs_m2_12m: 2173.0,
    		aantal_6m: 21,
    		aantal_12m: 40,
    	},
    	{
    		plaats: "Swalmen",
    		prijs_6m: 277179.0,
    		prijs_m2_6m: 2083.0,
    		prijs_12m: 260963.0,
    		prijs_m2_12m: 2035.0,
    		aantal_6m: 50,
    		aantal_12m: 110,
    	},
    	{
    		plaats: "Aalten",
    		prijs_6m: 274188.0,
    		prijs_m2_6m: 2138.0,
    		prijs_12m: 262089.0,
    		prijs_m2_12m: 2056.0,
    		aantal_6m: 84,
    		aantal_12m: 171,
    	},
    	{
    		plaats: "'t Harde",
    		prijs_6m: 271144.0,
    		prijs_m2_6m: 2410.0,
    		prijs_12m: 276600.0,
    		prijs_m2_12m: 2444.0,
    		aantal_6m: 48,
    		aantal_12m: 89,
    	},
    	{
    		plaats: "Achthuizen",
    		prijs_6m: 269991.0,
    		prijs_m2_6m: 2206.0,
    		prijs_12m: 280383.0,
    		prijs_m2_12m: 2210.0,
    		aantal_6m: 12,
    		aantal_12m: 18,
    	},
    	{
    		plaats: "Anna Paulowna",
    		prijs_6m: 268161.0,
    		prijs_m2_6m: 2146.0,
    		prijs_12m: 275355.0,
    		prijs_m2_12m: 2115.0,
    		aantal_6m: 67,
    		aantal_12m: 110,
    	},
    	{
    		plaats: "Waardenburg",
    		prijs_6m: 266407.0,
    		prijs_m2_6m: 2031.0,
    		prijs_12m: 264318.0,
    		prijs_m2_12m: 2051.0,
    		aantal_6m: 27,
    		aantal_12m: 58,
    	},
    	{
    		plaats: "'s-Heer Hendrikskinderen",
    		prijs_6m: 259666.0,
    		prijs_m2_6m: 2245.0,
    		prijs_12m: 267333.0,
    		prijs_m2_12m: 2252.0,
    		aantal_6m: 12,
    		aantal_12m: 18,
    	},
    	{
    		plaats: "Aduard",
    		prijs_6m: 258989.0,
    		prijs_m2_6m: 1956.0,
    		prijs_12m: 257450.0,
    		prijs_m2_12m: 1883.0,
    		aantal_6m: 19,
    		aantal_12m: 34,
    	},
    	{
    		plaats: "Alteveer (GR)",
    		prijs_6m: 254450.0,
    		prijs_m2_6m: 2114.0,
    		prijs_12m: 230722.0,
    		prijs_m2_12m: 1880.0,
    		aantal_6m: 10,
    		aantal_12m: 27,
    	},
    	{
    		plaats: "Aadorp",
    		prijs_6m: 251642.0,
    		prijs_m2_6m: 2002.0,
    		prijs_12m: 245218.0,
    		prijs_m2_12m: 2022.0,
    		aantal_6m: 7,
    		aantal_12m: 16,
    	},
    	{
    		plaats: "Julianadorp",
    		prijs_6m: 248789.0,
    		prijs_m2_6m: 2000.0,
    		prijs_12m: 247642.0,
    		prijs_m2_12m: 1988.0,
    		aantal_6m: 154,
    		aantal_12m: 279,
    	},
    	{
    		plaats: "Appelscha",
    		prijs_6m: 243959.0,
    		prijs_m2_6m: 1926.0,
    		prijs_12m: 268435.0,
    		prijs_m2_12m: 1992.0,
    		aantal_6m: 49,
    		aantal_12m: 70,
    	},
    	{
    		plaats: "Almelo",
    		prijs_6m: 230027.0,
    		prijs_m2_6m: 1914.0,
    		prijs_12m: 223629.0,
    		prijs_m2_12m: 1860.0,
    		aantal_6m: 412,
    		aantal_12m: 879,
    	},
    	{
    		plaats: "'s-Gravenpolder",
    		prijs_6m: 221145.0,
    		prijs_m2_6m: 1930.0,
    		prijs_12m: 226908.0,
    		prijs_m2_12m: 2040.0,
    		aantal_6m: 20,
    		aantal_12m: 50,
    	},
    	{
    		plaats: "'t Zandt",
    		prijs_6m: 216903.0,
    		prijs_m2_6m: 1795.0,
    		prijs_12m: 240795.0,
    		prijs_m2_12m: 2004.0,
    		aantal_6m: 26,
    		aantal_12m: 44,
    	},
    	{
    		plaats: "'s-Heerenberg",
    		prijs_6m: 205661.0,
    		prijs_m2_6m: 1881.0,
    		prijs_12m: 204701.0,
    		prijs_m2_12m: 1836.0,
    		aantal_6m: 34,
    		aantal_12m: 62,
    	},
    	{
    		plaats: "2e Exloërmond",
    		prijs_6m: 199222.0,
    		prijs_m2_6m: 1472.0,
    		prijs_12m: 200105.0,
    		prijs_m2_12m: 1528.0,
    		aantal_6m: 18,
    		aantal_12m: 38,
    	},
    	{
    		plaats: "'t Zandt",
    		prijs_6m: 191187.0,
    		prijs_m2_6m: 1203.0,
    		prijs_12m: 173863.0,
    		prijs_m2_12m: 1273.0,
    		aantal_6m: 8,
    		aantal_12m: 11,
    	},
    	{
    		plaats: "Aldeboarn",
    		prijs_6m: 185227.0,
    		prijs_m2_6m: 1753.0,
    		prijs_12m: 196694.0,
    		prijs_m2_12m: 1790.0,
    		aantal_6m: 11,
    		aantal_12m: 18,
    	},
    	{
    		plaats: "Anjum",
    		prijs_6m: 164166.0,
    		prijs_m2_6m: 1448.0,
    		prijs_12m: 174111.0,
    		prijs_m2_12m: 1320.0,
    		aantal_6m: 3,
    		aantal_12m: 9,
    	},
    ];

    $scope.alleBuurtenTotallist = [
    	{
    		postcode: 1071,
    		plaats: "Amsterdam",
    		prijs_6m: 1507855.0,
    		prijs_m2_6m: 8628.0,
    		prijs_12m: 1412684.0,
    		prijs_m2_12m: 8599.0,
    		aantal_6m: 83,
    		aantal_12m: 165,
    	},
    	{
    		postcode: 1017,
    		plaats: "Amsterdam",
    		prijs_6m: 1029059.0,
    		prijs_m2_6m: 8088.0,
    		prijs_12m: 900947.0,
    		prijs_m2_12m: 7853.0,
    		aantal_6m: 93,
    		aantal_12m: 180,
    	},
    	{
    		postcode: 1077,
    		plaats: "Amsterdam",
    		prijs_6m: 966151.0,
    		prijs_m2_6m: 7049.0,
    		prijs_12m: 1025143.0,
    		prijs_m2_12m: 7063.0,
    		aantal_6m: 66,
    		aantal_12m: 136,
    	},
    	{
    		postcode: 1182,
    		plaats: "Amstelveen",
    		prijs_6m: 948500.0,
    		prijs_m2_6m: 6165.0,
    		prijs_12m: 863737.0,
    		prijs_m2_12m: 5589.0,
    		aantal_6m: 18,
    		aantal_12m: 38,
    	},
    	{
    		postcode: 1016,
    		plaats: "Amsterdam",
    		prijs_6m: 932359.0,
    		prijs_m2_6m: 7818.0,
    		prijs_12m: 902265.0,
    		prijs_m2_12m: 7851.0,
    		aantal_6m: 81,
    		aantal_12m: 162,
    	},
    	{
    		postcode: 1028,
    		plaats: "Amsterdam",
    		prijs_6m: 885000.0,
    		prijs_m2_6m: 5624.0,
    		prijs_12m: 834166.0,
    		prijs_m2_12m: 5352.0,
    		aantal_6m: 5,
    		aantal_12m: 6,
    	},
    	{
    		postcode: 1012,
    		plaats: "Amsterdam",
    		prijs_6m: 845547.0,
    		prijs_m2_6m: 7109.0,
    		prijs_12m: 762658.0,
    		prijs_m2_12m: 7454.0,
    		aantal_6m: 42,
    		aantal_12m: 79,
    	},
    	{
    		postcode: 1075,
    		plaats: "Amsterdam",
    		prijs_6m: 821878.0,
    		prijs_m2_6m: 7559.0,
    		prijs_12m: 815999.0,
    		prijs_m2_12m: 7491.0,
    		aantal_6m: 66,
    		aantal_12m: 136,
    	},
    	{
    		postcode: 1014,
    		plaats: "Amsterdam",
    		prijs_6m: 821446.0,
    		prijs_m2_6m: 6454.0,
    		prijs_12m: 765757.0,
    		prijs_m2_12m: 6549.0,
    		aantal_6m: 66,
    		aantal_12m: 103,
    	},
    	{
    		postcode: 1081,
    		plaats: "Amsterdam",
    		prijs_6m: 755987.0,
    		prijs_m2_6m: 5418.0,
    		prijs_12m: 638963.0,
    		prijs_m2_12m: 5298.0,
    		aantal_6m: 40,
    		aantal_12m: 83,
    	},
    	{
    		postcode: 1074,
    		plaats: "Amsterdam",
    		prijs_6m: 755211.0,
    		prijs_m2_6m: 7616.0,
    		prijs_12m: 685813.0,
    		prijs_m2_12m: 7298.0,
    		aantal_6m: 71,
    		aantal_12m: 118,
    	},
    	{
    		postcode: 1015,
    		plaats: "Amsterdam",
    		prijs_6m: 699777.0,
    		prijs_m2_6m: 7757.0,
    		prijs_12m: 687795.0,
    		prijs_m2_12m: 7616.0,
    		aantal_6m: 101,
    		aantal_12m: 210,
    	},
    	{
    		postcode: 1054,
    		plaats: "Amsterdam",
    		prijs_6m: 693858.0,
    		prijs_m2_6m: 7242.0,
    		prijs_12m: 683009.0,
    		prijs_m2_12m: 7175.0,
    		aantal_6m: 155,
    		aantal_12m: 275,
    	},
    	{
    		postcode: 1011,
    		plaats: "Amsterdam",
    		prijs_6m: 689747.0,
    		prijs_m2_6m: 7001.0,
    		prijs_12m: 660587.0,
    		prijs_m2_12m: 6976.0,
    		aantal_6m: 51,
    		aantal_12m: 121,
    	},
    	{
    		postcode: 1078,
    		plaats: "Amsterdam",
    		prijs_6m: 633917.0,
    		prijs_m2_6m: 6448.0,
    		prijs_12m: 598973.0,
    		prijs_m2_12m: 6333.0,
    		aantal_6m: 97,
    		aantal_12m: 188,
    	},
    	{
    		postcode: 1151,
    		plaats: "Broek in Waterland",
    		prijs_6m: 631571.0,
    		prijs_m2_6m: 5305.0,
    		prijs_12m: 589472.0,
    		prijs_m2_12m: 4894.0,
    		aantal_6m: 21,
    		aantal_12m: 36,
    	},
    	{
    		postcode: 1181,
    		plaats: "Amstelveen",
    		prijs_6m: 625794.0,
    		prijs_m2_6m: 5187.0,
    		prijs_12m: 593247.0,
    		prijs_m2_12m: 5003.0,
    		aantal_6m: 188,
    		aantal_12m: 353,
    	},
    	{
    		postcode: 1184,
    		plaats: "Amstelveen",
    		prijs_6m: 606250.0,
    		prijs_m2_6m: 5277.0,
    		prijs_12m: 608333.0,
    		prijs_m2_12m: 5543.0,
    		aantal_6m: 4,
    		aantal_12m: 6,
    	},
    	{
    		postcode: 1086,
    		plaats: "Amsterdam",
    		prijs_6m: 590802.0,
    		prijs_m2_6m: 4539.0,
    		prijs_12m: 607630.0,
    		prijs_m2_12m: 4445.0,
    		aantal_6m: 38,
    		aantal_12m: 65,
    	},
    	{
    		postcode: 1018,
    		plaats: "Amsterdam",
    		prijs_6m: 575303.0,
    		prijs_m2_6m: 6852.0,
    		prijs_12m: 566813.0,
    		prijs_m2_12m: 6661.0,
    		aantal_6m: 153,
    		aantal_12m: 268,
    	},
    	{
    		postcode: 1127,
    		plaats: "Den Ilp",
    		prijs_6m: 573750.0,
    		prijs_m2_6m: 4777.0,
    		prijs_12m: 541144.0,
    		prijs_m2_12m: 4492.0,
    		aantal_6m: 4,
    		aantal_12m: 7,
    	},
    	{
    		postcode: 1019,
    		plaats: "Amsterdam",
    		prijs_6m: 570889.0,
    		prijs_m2_6m: 5393.0,
    		prijs_12m: 546116.0,
    		prijs_m2_12m: 5416.0,
    		aantal_6m: 118,
    		aantal_12m: 261,
    	},
    	{
    		postcode: 1079,
    		plaats: "Amsterdam",
    		prijs_6m: 563231.0,
    		prijs_m2_6m: 6456.0,
    		prijs_12m: 526778.0,
    		prijs_m2_12m: 6352.0,
    		aantal_6m: 93,
    		aantal_12m: 187,
    	},
    	{
    		postcode: 1096,
    		plaats: "Amsterdam",
    		prijs_6m: 562929.0,
    		prijs_m2_6m: 6763.0,
    		prijs_12m: 671592.0,
    		prijs_m2_12m: 6920.0,
    		aantal_6m: 28,
    		aantal_12m: 65,
    	},
    	{
    		postcode: 1087,
    		plaats: "Amsterdam",
    		prijs_6m: 562390.0,
    		prijs_m2_6m: 4403.0,
    		prijs_12m: 536898.0,
    		prijs_m2_12m: 4380.0,
    		aantal_6m: 141,
    		aantal_12m: 252,
    	},
    	{
    		postcode: 1023,
    		plaats: "Amsterdam",
    		prijs_6m: 559937.0,
    		prijs_m2_6m: 5592.0,
    		prijs_12m: 440406.0,
    		prijs_m2_12m: 5241.0,
    		aantal_6m: 16,
    		aantal_12m: 48,
    	},
    	{
    		postcode: 1098,
    		plaats: "Amsterdam",
    		prijs_6m: 555901.0,
    		prijs_m2_6m: 5626.0,
    		prijs_12m: 576167.0,
    		prijs_m2_12m: 5610.0,
    		aantal_6m: 97,
    		aantal_12m: 175,
    	},
    	{
    		postcode: 1109,
    		plaats: "Amsterdam",
    		prijs_6m: 552500.0,
    		prijs_m2_6m: 3991.0,
    		prijs_12m: 469153.0,
    		prijs_m2_12m: 3779.0,
    		aantal_6m: 6,
    		aantal_12m: 13,
    	},
    	{
    		postcode: 1187,
    		plaats: "Amstelveen",
    		prijs_6m: 544111.0,
    		prijs_m2_6m: 4237.0,
    		prijs_12m: 530571.0,
    		prijs_m2_12m: 4236.0,
    		aantal_6m: 99,
    		aantal_12m: 187,
    	},
    	{
    		postcode: 1058,
    		plaats: "Amsterdam",
    		prijs_6m: 533470.0,
    		prijs_m2_6m: 6287.0,
    		prijs_12m: 521523.0,
    		prijs_m2_12m: 6150.0,
    		aantal_6m: 137,
    		aantal_12m: 278,
    	},
    	{
    		postcode: 1059,
    		plaats: "Amsterdam",
    		prijs_6m: 530477.0,
    		prijs_m2_6m: 6436.0,
    		prijs_12m: 505247.0,
    		prijs_m2_12m: 6343.0,
    		aantal_6m: 67,
    		aantal_12m: 113,
    	},
    	{
    		postcode: 1043,
    		plaats: "Amsterdam",
    		prijs_6m: 527216.0,
    		prijs_m2_6m: 5600.0,
    		prijs_12m: 550434.0,
    		prijs_m2_12m: 5351.0,
    		aantal_6m: 5,
    		aantal_12m: 14,
    	},
    	{
    		postcode: 1031,
    		plaats: "Amsterdam",
    		prijs_6m: 519961.0,
    		prijs_m2_6m: 6015.0,
    		prijs_12m: 557775.0,
    		prijs_m2_12m: 6116.0,
    		aantal_6m: 111,
    		aantal_12m: 183,
    	},
    	{
    		postcode: 1113,
    		plaats: "Diemen",
    		prijs_6m: 516703.0,
    		prijs_m2_6m: 3823.0,
    		prijs_12m: 555954.0,
    		prijs_m2_12m: 3875.0,
    		aantal_6m: 10,
    		aantal_12m: 21,
    	},
    	{
    		postcode: 1036,
    		plaats: "Amsterdam",
    		prijs_6m: 512750.0,
    		prijs_m2_6m: 3918.0,
    		prijs_12m: 358589.0,
    		prijs_m2_12m: 3799.0,
    		aantal_6m: 16,
    		aantal_12m: 90,
    	},
    	{
    		postcode: 1097,
    		plaats: "Amsterdam",
    		prijs_6m: 509542.0,
    		prijs_m2_6m: 5944.0,
    		prijs_12m: 504044.0,
    		prijs_m2_12m: 5770.0,
    		aantal_6m: 35,
    		aantal_12m: 68,
    	},
    	{
    		postcode: 1083,
    		plaats: "Amsterdam",
    		prijs_6m: 501226.0,
    		prijs_m2_6m: 5403.0,
    		prijs_12m: 474776.0,
    		prijs_m2_12m: 5162.0,
    		aantal_6m: 57,
    		aantal_12m: 101,
    	},
    	{
    		postcode: 1052,
    		plaats: "Amsterdam",
    		prijs_6m: 499228.0,
    		prijs_m2_6m: 6465.0,
    		prijs_12m: 482471.0,
    		prijs_m2_12m: 6387.0,
    		aantal_6m: 81,
    		aantal_12m: 175,
    	},
    	{
    		postcode: 1022,
    		plaats: "Amsterdam",
    		prijs_6m: 497882.0,
    		prijs_m2_6m: 4681.0,
    		prijs_12m: 473030.0,
    		prijs_m2_12m: 4696.0,
    		aantal_6m: 17,
    		aantal_12m: 33,
    	},
    	{
    		postcode: 1082,
    		plaats: "Amsterdam",
    		prijs_6m: 494218.0,
    		prijs_m2_6m: 5600.0,
    		prijs_12m: 476225.0,
    		prijs_m2_12m: 5722.0,
    		aantal_6m: 112,
    		aantal_12m: 213,
    	},
    	{
    		postcode: 1092,
    		plaats: "Amsterdam",
    		prijs_6m: 493925.0,
    		prijs_m2_6m: 6162.0,
    		prijs_12m: 475359.0,
    		prijs_m2_12m: 6150.0,
    		aantal_6m: 60,
    		aantal_12m: 103,
    	},
    	{
    		postcode: 1013,
    		plaats: "Amsterdam",
    		prijs_6m: 489745.0,
    		prijs_m2_6m: 6287.0,
    		prijs_12m: 512853.0,
    		prijs_m2_12m: 6223.0,
    		aantal_6m: 143,
    		aantal_12m: 284,
    	},
    	{
    		postcode: 1073,
    		plaats: "Amsterdam",
    		prijs_6m: 489142.0,
    		prijs_m2_6m: 7005.0,
    		prijs_12m: 493906.0,
    		prijs_m2_12m: 6876.0,
    		aantal_6m: 88,
    		aantal_12m: 153,
    	},
    	{
    		postcode: 1091,
    		plaats: "Amsterdam",
    		prijs_6m: 485392.0,
    		prijs_m2_6m: 6295.0,
    		prijs_12m: 456554.0,
    		prijs_m2_12m: 6309.0,
    		aantal_6m: 111,
    		aantal_12m: 219,
    	},
    	{
    		postcode: 1076,
    		plaats: "Amsterdam",
    		prijs_6m: 483793.0,
    		prijs_m2_6m: 6542.0,
    		prijs_12m: 463574.0,
    		prijs_m2_12m: 6628.0,
    		aantal_6m: 58,
    		aantal_12m: 108,
    	},
    	{
    		postcode: 1066,
    		plaats: "Amsterdam",
    		prijs_6m: 471050.0,
    		prijs_m2_6m: 4165.0,
    		prijs_12m: 431014.0,
    		prijs_m2_12m: 3984.0,
    		aantal_6m: 60,
    		aantal_12m: 131,
    	},
    	{
    		postcode: 1141,
    		plaats: "Monnickendam",
    		prijs_6m: 470928.0,
    		prijs_m2_6m: 3617.0,
    		prijs_12m: 436265.0,
    		prijs_m2_12m: 3489.0,
    		aantal_6m: 42,
    		aantal_12m: 83,
    	},
    	{
    		postcode: 1035,
    		plaats: "Amsterdam",
    		prijs_6m: 462069.0,
    		prijs_m2_6m: 3881.0,
    		prijs_12m: 447879.0,
    		prijs_m2_12m: 3849.0,
    		aantal_6m: 72,
    		aantal_12m: 124,
    	},
    	{
    		postcode: 1072,
    		plaats: "Amsterdam",
    		prijs_6m: 456695.0,
    		prijs_m2_6m: 7114.0,
    		prijs_12m: 491277.0,
    		prijs_m2_12m: 6956.0,
    		aantal_6m: 92,
    		aantal_12m: 189,
    	},
    	{
    		postcode: 1186,
    		plaats: "Amstelveen",
    		prijs_6m: 454157.0,
    		prijs_m2_6m: 3979.0,
    		prijs_12m: 441863.0,
    		prijs_m2_12m: 4005.0,
    		aantal_6m: 63,
    		aantal_12m: 122,
    	},
    	{
    		postcode: 1121,
    		plaats: "Landsmeer",
    		prijs_6m: 451470.0,
    		prijs_m2_6m: 4155.0,
    		prijs_12m: 450737.0,
    		prijs_m2_12m: 4099.0,
    		aantal_6m: 44,
    		aantal_12m: 92,
    	},
    	{
    		postcode: 1093,
    		plaats: "Amsterdam",
    		prijs_6m: 450136.0,
    		prijs_m2_6m: 5853.0,
    		prijs_12m: 411000.0,
    		prijs_m2_12m: 5743.0,
    		aantal_6m: 55,
    		aantal_12m: 118,
    	},
    	{
    		postcode: 1171,
    		plaats: "Badhoevedorp",
    		prijs_6m: 449129.0,
    		prijs_m2_6m: 3886.0,
    		prijs_12m: 453323.0,
    		prijs_m2_12m: 3811.0,
    		aantal_6m: 77,
    		aantal_12m: 172,
    	},
    	{
    		postcode: 1032,
    		plaats: "Amsterdam",
    		prijs_6m: 446637.0,
    		prijs_m2_6m: 5191.0,
    		prijs_12m: 399609.0,
    		prijs_m2_12m: 5043.0,
    		aantal_6m: 37,
    		aantal_12m: 74,
    	},
    	{
    		postcode: 1183,
    		plaats: "Amstelveen",
    		prijs_6m: 445080.0,
    		prijs_m2_6m: 4368.0,
    		prijs_12m: 452805.0,
    		prijs_m2_12m: 4307.0,
    		aantal_6m: 61,
    		aantal_12m: 127,
    	},
    	{
    		postcode: 1053,
    		plaats: "Amsterdam",
    		prijs_6m: 440402.0,
    		prijs_m2_6m: 7020.0,
    		prijs_12m: 438396.0,
    		prijs_m2_12m: 6906.0,
    		aantal_6m: 133,
    		aantal_12m: 231,
    	},
    	{
    		postcode: 1188,
    		plaats: "Amstelveen",
    		prijs_6m: 438500.0,
    		prijs_m2_6m: 4035.0,
    		prijs_12m: 450891.0,
    		prijs_m2_12m: 4027.0,
    		aantal_6m: 33,
    		aantal_12m: 60,
    	},
    	{
    		postcode: 1095,
    		plaats: "Amsterdam",
    		prijs_6m: 436983.0,
    		prijs_m2_6m: 5476.0,
    		prijs_12m: 422628.0,
    		prijs_m2_12m: 5382.0,
    		aantal_6m: 76,
    		aantal_12m: 138,
    	},
    	{
    		postcode: 1060,
    		plaats: "Amsterdam",
    		prijs_6m: 419166.0,
    		prijs_m2_6m: 3486.0,
    		prijs_12m: 427290.0,
    		prijs_m2_12m: 3424.0,
    		aantal_6m: 48,
    		aantal_12m: 99,
    	},
    	{
    		postcode: 1185,
    		plaats: "Amstelveen",
    		prijs_6m: 419152.0,
    		prijs_m2_6m: 4299.0,
    		prijs_12m: 431261.0,
    		prijs_m2_12m: 4272.0,
    		aantal_6m: 72,
    		aantal_12m: 147,
    	},
    	{
    		postcode: 1132,
    		plaats: "Volendam",
    		prijs_6m: 418172.0,
    		prijs_m2_6m: 2902.0,
    		prijs_12m: 390631.0,
    		prijs_m2_12m: 2778.0,
    		aantal_6m: 36,
    		aantal_12m: 70,
    	},
    	{
    		postcode: 1057,
    		plaats: "Amsterdam",
    		prijs_6m: 413567.0,
    		prijs_m2_6m: 6072.0,
    		prijs_12m: 414580.0,
    		prijs_m2_12m: 6004.0,
    		aantal_6m: 93,
    		aantal_12m: 184,
    	},
    	{
    		postcode: 1115,
    		plaats: "Duivendrecht",
    		prijs_6m: 404473.0,
    		prijs_m2_6m: 3881.0,
    		prijs_12m: 412068.0,
    		prijs_m2_12m: 3844.0,
    		aantal_6m: 19,
    		aantal_12m: 44,
    	},
    	{
    		postcode: 1051,
    		plaats: "Amsterdam",
    		prijs_6m: 392554.0,
    		prijs_m2_6m: 6375.0,
    		prijs_12m: 404105.0,
    		prijs_m2_12m: 6355.0,
    		aantal_6m: 92,
    		aantal_12m: 211,
    	},
    	{
    		postcode: 1175,
    		plaats: "Lijnden",
    		prijs_6m: 392400.0,
    		prijs_m2_6m: 3866.0,
    		prijs_12m: 404208.0,
    		prijs_m2_12m: 3614.0,
    		aantal_6m: 5,
    		aantal_12m: 12,
    	},
    	{
    		postcode: 1112,
    		plaats: "Diemen",
    		prijs_6m: 386110.0,
    		prijs_m2_6m: 4275.0,
    		prijs_12m: 366086.0,
    		prijs_m2_12m: 4105.0,
    		aantal_6m: 44,
    		aantal_12m: 87,
    	},
    	{
    		postcode: 1056,
    		plaats: "Amsterdam",
    		prijs_6m: 382046.0,
    		prijs_m2_6m: 5995.0,
    		prijs_12m: 375379.0,
    		prijs_m2_12m: 5920.0,
    		aantal_6m: 165,
    		aantal_12m: 319,
    	},
    	{
    		postcode: 1161,
    		plaats: "Zwanenburg",
    		prijs_6m: 376951.0,
    		prijs_m2_6m: 3406.0,
    		prijs_12m: 332522.0,
    		prijs_m2_12m: 3296.0,
    		aantal_6m: 42,
    		aantal_12m: 88,
    	},
    	{
    		postcode: 1111,
    		plaats: "Diemen",
    		prijs_6m: 374768.0,
    		prijs_m2_6m: 3937.0,
    		prijs_12m: 382232.0,
    		prijs_m2_12m: 3913.0,
    		aantal_6m: 90,
    		aantal_12m: 184,
    	},
    	{
    		postcode: 1135,
    		plaats: "Edam",
    		prijs_6m: 373022.0,
    		prijs_m2_6m: 3561.0,
    		prijs_12m: 367871.0,
    		prijs_m2_12m: 3454.0,
    		aantal_6m: 44,
    		aantal_12m: 74,
    	},
    	{
    		postcode: 1062,
    		plaats: "Amsterdam",
    		prijs_6m: 371662.0,
    		prijs_m2_6m: 4631.0,
    		prijs_12m: 371536.0,
    		prijs_m2_12m: 4612.0,
    		aantal_6m: 45,
    		aantal_12m: 90,
    	},
    	{
    		postcode: 1165,
    		plaats: "Halfweg",
    		prijs_6m: 370750.0,
    		prijs_m2_6m: 3391.0,
    		prijs_12m: 400384.0,
    		prijs_m2_12m: 3516.0,
    		aantal_6m: 12,
    		aantal_12m: 39,
    	},
    	{
    		postcode: 1061,
    		plaats: "Amsterdam",
    		prijs_6m: 366613.0,
    		prijs_m2_6m: 4652.0,
    		prijs_12m: 388468.0,
    		prijs_m2_12m: 4457.0,
    		aantal_6m: 53,
    		aantal_12m: 106,
    	},
    	{
    		postcode: 1021,
    		plaats: "Amsterdam",
    		prijs_6m: 366593.0,
    		prijs_m2_6m: 4732.0,
    		prijs_12m: 359608.0,
    		prijs_m2_12m: 4821.0,
    		aantal_6m: 32,
    		aantal_12m: 51,
    	},
    	{
    		postcode: 1094,
    		plaats: "Amsterdam",
    		prijs_6m: 356978.0,
    		prijs_m2_6m: 5966.0,
    		prijs_12m: 357882.0,
    		prijs_m2_12m: 5993.0,
    		aantal_6m: 92,
    		aantal_12m: 192,
    	},
    	{
    		postcode: 1067,
    		plaats: "Amsterdam",
    		prijs_6m: 353437.0,
    		prijs_m2_6m: 3690.0,
    		prijs_12m: 335233.0,
    		prijs_m2_12m: 3530.0,
    		aantal_6m: 56,
    		aantal_12m: 118,
    	},
    	{
    		postcode: 1055,
    		plaats: "Amsterdam",
    		prijs_6m: 347991.0,
    		prijs_m2_6m: 5596.0,
    		prijs_12m: 346302.0,
    		prijs_m2_12m: 5554.0,
    		aantal_6m: 175,
    		aantal_12m: 383,
    	},
    	{
    		postcode: 1033,
    		plaats: "Amsterdam",
    		prijs_6m: 343183.0,
    		prijs_m2_6m: 4327.0,
    		prijs_12m: 322195.0,
    		prijs_m2_12m: 4109.0,
    		aantal_6m: 59,
    		aantal_12m: 122,
    	},
    	{
    		postcode: 1064,
    		plaats: "Amsterdam",
    		prijs_6m: 325666.0,
    		prijs_m2_6m: 4102.0,
    		prijs_12m: 323757.0,
    		prijs_m2_12m: 4158.0,
    		aantal_6m: 60,
    		aantal_12m: 126,
    	},
    	{
    		postcode: 1065,
    		plaats: "Amsterdam",
    		prijs_6m: 322527.0,
    		prijs_m2_6m: 4170.0,
    		prijs_12m: 322175.0,
    		prijs_m2_12m: 4201.0,
    		aantal_6m: 55,
    		aantal_12m: 93,
    	},
    	{
    		postcode: 1025,
    		plaats: "Amsterdam",
    		prijs_6m: 317608.0,
    		prijs_m2_6m: 3744.0,
    		prijs_12m: 319061.0,
    		prijs_m2_12m: 3698.0,
    		aantal_6m: 81,
    		aantal_12m: 159,
    	},
    	{
    		postcode: 1069,
    		plaats: "Amsterdam",
    		prijs_6m: 317478.0,
    		prijs_m2_6m: 3386.0,
    		prijs_12m: 308860.0,
    		prijs_m2_12m: 3395.0,
    		aantal_6m: 126,
    		aantal_12m: 217,
    	},
    	{
    		postcode: 1034,
    		plaats: "Amsterdam",
    		prijs_6m: 315198.0,
    		prijs_m2_6m: 3736.0,
    		prijs_12m: 334023.0,
    		prijs_m2_12m: 3772.0,
    		aantal_6m: 73,
    		aantal_12m: 126,
    	},
    	{
    		postcode: 1068,
    		plaats: "Amsterdam",
    		prijs_6m: 314612.0,
    		prijs_m2_6m: 3691.0,
    		prijs_12m: 312445.0,
    		prijs_m2_12m: 3687.0,
    		aantal_6m: 80,
    		aantal_12m: 159,
    	},
    	{
    		postcode: 1024,
    		plaats: "Amsterdam",
    		prijs_6m: 310608.0,
    		prijs_m2_6m: 3653.0,
    		prijs_12m: 297718.0,
    		prijs_m2_12m: 3621.0,
    		aantal_6m: 72,
    		aantal_12m: 127,
    	},
    	{
    		postcode: 1104,
    		plaats: "Amsterdam",
    		prijs_6m: 289112.0,
    		prijs_m2_6m: 3191.0,
    		prijs_12m: 278365.0,
    		prijs_m2_12m: 3166.0,
    		aantal_6m: 46,
    		aantal_12m: 92,
    	},
    	{
    		postcode: 1063,
    		plaats: "Amsterdam",
    		prijs_6m: 285951.0,
    		prijs_m2_6m: 3982.0,
    		prijs_12m: 283979.0,
    		prijs_m2_12m: 3868.0,
    		aantal_6m: 61,
    		aantal_12m: 109,
    	},
    	{
    		postcode: 1131,
    		plaats: "Volendam",
    		prijs_6m: 282009.0,
    		prijs_m2_6m: 3007.0,
    		prijs_12m: 279611.0,
    		prijs_m2_12m: 2988.0,
    		aantal_6m: 51,
    		aantal_12m: 94,
    	},
    	{
    		postcode: 1103,
    		plaats: "Amsterdam",
    		prijs_6m: 280169.0,
    		prijs_m2_6m: 2988.0,
    		prijs_12m: 269868.0,
    		prijs_m2_12m: 2940.0,
    		aantal_6m: 52,
    		aantal_12m: 103,
    	},
    	{
    		postcode: 1106,
    		plaats: "Amsterdam",
    		prijs_6m: 263060.0,
    		prijs_m2_6m: 2956.0,
    		prijs_12m: 256390.0,
    		prijs_m2_12m: 2936.0,
    		aantal_6m: 66,
    		aantal_12m: 119,
    	},
    	{
    		postcode: 1102,
    		plaats: "Amsterdam",
    		prijs_6m: 254885.0,
    		prijs_m2_6m: 3490.0,
    		prijs_12m: 249750.0,
    		prijs_m2_12m: 3439.0,
    		aantal_6m: 87,
    		aantal_12m: 162,
    	},
    	{
    		postcode: 1156,
    		plaats: "Marken",
    		prijs_6m: 253909.0,
    		prijs_m2_6m: 3224.0,
    		prijs_12m: 292552.0,
    		prijs_m2_12m: 3219.0,
    		aantal_6m: 11,
    		aantal_12m: 19,
    	},
    	{
    		postcode: 1108,
    		plaats: "Amsterdam",
    		prijs_6m: 241857.0,
    		prijs_m2_6m: 3165.0,
    		prijs_12m: 241030.0,
    		prijs_m2_12m: 3091.0,
    		aantal_6m: 28,
    		aantal_12m: 50,
    	},
    	{
    		postcode: 1107,
    		plaats: "Amsterdam",
    		prijs_6m: 229223.0,
    		prijs_m2_6m: 3004.0,
    		prijs_12m: 234545.0,
    		prijs_m2_12m: 2920.0,
    		aantal_6m: 47,
    		aantal_12m: 88,
    	},
    ];
});
