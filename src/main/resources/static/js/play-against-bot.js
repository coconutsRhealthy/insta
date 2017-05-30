var mainApp = angular.module("mainApp", []);

mainApp.controller('pokerController', function($scope, $http) {

    $scope.computerGame;

    $scope.holeCard1ConvertedRank;
    $scope.holeCard2ConvertedRank;
    $scope.flopCard1ConvertedRank;
    $scope.flopCard2ConvertedRank;
    $scope.flopCard3ConvertedRank;
    $scope.turnCardConvertedRank;
    $scope.riverCardConvertedRank;

    $scope.holeCard1SuitWritten;
    $scope.holeCard2SuitWritten;
    $scope.flopCard1SuitWritten;
    $scope.flopCard2SuitWritten;
    $scope.flopCard3SuitWritten;
    $scope.turnCardSuitWritten;
    $scope.riverCardSuitWritten;

    $scope.holeCard1Class;
    $scope.holeCard1SuitUniCode;

    $scope.holeCard2Class;
    $scope.holeCard2SuitUniCode;

    $scope.flopCard1Class;
    $scope.flopCard1SuitUniCode;

    $scope.flopCard2Class;
    $scope.flopCard2SuitUniCode;

    $scope.flopCard3Class;
    $scope.flopCard3SuitUniCode;

    $scope.turnCardClass;
    $scope.turnCardSuitUniCode;

    $scope.riverCardClass;
    $scope.riverCardSuitUniCode;

    $scope.dealerButtonStyle;

    $scope.fold = "fold";
    $scope.check = "check";
    $scope.call = "call";
    $scope.bet = "bet";
    $scope.raise = "raise";

    $scope.disableFoldButton;
    $scope.disableCheckButton;
    $scope.disableCallButton;
    $scope.disableBetButton;
    $scope.disableRaiseButton;

    $scope.showNextHandButton;

    $scope.showGame;

    $scope.startGame = function() {
        alert("hoihoi");
        $http.get('http://nieuws-statistieken.nl:8080/headlines-1.0-SNAPSHOT/startGame').success(function(data) {
            alert("heeee");
            alert(JSON.stringify(data));
        })
    }

    function setScopePropertiesCorrect(data) {
        $scope.computerGame = data;

        $scope.holeCard1ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.myHoleCards[0].rank);
        $scope.holeCard2ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.myHoleCards[1].rank);

        setSuitWrittenAndUniCode("holeCard1SuitWritten", "holeCard1SuitUniCode", $scope.computerGame.myHoleCards[0].suit);
        setSuitWrittenAndUniCode("holeCard2SuitWritten", "holeCard2SuitUniCode", $scope.computerGame.myHoleCards[1].suit);

        $scope.holeCard1Class = "card rank-" + $scope.holeCard1ConvertedRank + " " + $scope.holeCard1SuitWritten;
        $scope.holeCard2Class = "card rank-" + $scope.holeCard2ConvertedRank + " " + $scope.holeCard2SuitWritten;

        if($scope.computerGame.flopCards != undefined) {
            $scope.flopCard1ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.flopCards[0].rank);
            $scope.flopCard2ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.flopCards[1].rank);
            $scope.flopCard3ConvertedRank = convertRankFromIntegerToRank($scope.computerGame.flopCards[2].rank);

            setSuitWrittenAndUniCode("flopCard1SuitWritten", "flopCard1SuitUniCode", $scope.computerGame.flopCards[0].suit);
            setSuitWrittenAndUniCode("flopCard2SuitWritten", "flopCard2SuitUniCode", $scope.computerGame.flopCards[1].suit);
            setSuitWrittenAndUniCode("flopCard3SuitWritten", "flopCard3SuitUniCode", $scope.computerGame.flopCards[2].suit);

            $scope.flopCard1Class = "card rank-" + $scope.flopCard1ConvertedRank + " " + $scope.flopCard1SuitWritten;
            $scope.flopCard2Class = "card rank-" + $scope.flopCard2ConvertedRank + " " + $scope.flopCard2SuitWritten;
            $scope.flopCard3Class = "card rank-" + $scope.flopCard3ConvertedRank + " " + $scope.flopCard3SuitWritten;
        } else {
            resetFlopCards();
        }

        if($scope.computerGame.turnCard != undefined) {
            $scope.turnCardConvertedRank = convertRankFromIntegerToRank($scope.computerGame.turnCard.rank);
            setSuitWrittenAndUniCode("turnCardSuitWritten", "turnCardSuitUniCode", $scope.computerGame.turnCard.suit);
            $scope.turnCardClass = "card rank-" + $scope.turnCardConvertedRank + " " + $scope.turnCardSuitWritten;
        } else {
            resetTurnCard();
        }

        if($scope.computerGame.riverCard != undefined) {
            $scope.riverCardConvertedRank = convertRankFromIntegerToRank($scope.computerGame.riverCard.rank);
            setSuitWrittenAndUniCode("riverCardSuitWritten", "riverCardSuitUniCode", $scope.computerGame.riverCard.suit);
            $scope.riverCardClass = "card rank-" + $scope.riverCardConvertedRank + " " + $scope.riverCardSuitWritten;
        } else {
            resetRiverCard();
        }

        setDealerButton();
        setWidthOfBoardCards();
        showNextHandButton();
        disableActionButtons();
    }

    function setSuitWrittenAndUniCode(scopeVariableSuitWritten, scopeVariableSuitUniCode, suit) {
        switch(suit) {
            case 's':
                $scope[scopeVariableSuitWritten] = "spades";
                $scope[scopeVariableSuitUniCode] = "\u2660";
                break;
            case 'c':
                $scope[scopeVariableSuitWritten] = "clubs";
                $scope[scopeVariableSuitUniCode] = "\u2663";
                break;
            case 'd':
                $scope[scopeVariableSuitWritten] = "diams";
                $scope[scopeVariableSuitUniCode] = "\u2666";
                break;
            case 'h':
                $scope[scopeVariableSuitWritten] = "hearts";
                $scope[scopeVariableSuitUniCode] = "\u2665";
                break;
        }
    }

    function convertRankFromIntegerToRank(rankCard) {
        switch(rankCard) {
            case 14:
                return 'A';
                break;
            case 13:
                return 'K';
                break;
            case 12:
                return 'Q';
                break;
            case 11:
                return 'J';
                break;
            default:
                return rankCard;
        }
    }

    function setDealerButton() {
        if($scope.computerGame.computerIsButton) {
            $scope.dealerButtonStyle = "float: right; padding-top: 30px;";
        } else {
            $scope.dealerButtonStyle = "float: left; padding-top: 30px;";
        }
    }

    function setWidthOfBoardCards() {
        if($scope.computerGame.flopCards != undefined) {
            if($scope.computerGame.turnCard != undefined) {
                if($scope.computerGame.riverCard != undefined) {
                    $scope.boardCardsStyle = "width: 430px;";
                    return;
                }
                $scope.boardCardsStyle = "width: 345px;";
                return;
            }
            $scope.boardCardsStyle = "width: 254px;";
            return;
        }
    }

    function resetFlopCards() {
        $scope.flopCard1ConvertedRank = null;
        $scope.flopCard2ConvertedRank = null;
        $scope.flopCard3ConvertedRank = null;

        $scope.flopCard1SuitUniCode = null;
        $scope.flopCard2SuitUniCode = null;
        $scope.flopCard3SuitUniCode = null;

        $scope.flopCard1Class = null;
        $scope.flopCard2Class = null;
        $scope.flopCard3Class = null;
    }

    function resetTurnCard() {
        $scope.turnCardConvertedRank = null;
        $scope.turnCardSuitUniCode = null;
        $scope.turnCardClass = null;
    }

    function resetRiverCard() {
        $scope.riverCardConvertedRank = null;
        $scope.riverCardSuitUniCode = null;
        $scope.riverCardClass = null;
    }

    function showNextHandButton() {
        if($scope.computerGame != null && $scope.computerGame.potSize != null
            && $scope.computerGame.computerTotalBetSize != null) {
            if($scope.computerGame.potSize != 0 && $scope.computerGame.computerTotalBetSize == 0) {
                if($scope.computerGame.computerWrittenAction != null) {
                    if($scope.computerGame.computerWrittenAction.includes("win") ||
                        $scope.computerGame.computerWrittenAction.includes("fold") ||
                        $scope.computerGame.computerWrittenAction.includes("Draw")) {
                        $scope.showNextHandButton = true;
                    }
                } else if($scope.computerGame.myAction != null) {
                    if($scope.computerGame.myAction.includes("fold")) {
                        $scope.showNextHandButton = true;
                    }
                } else {
                    $scope.showNextHandButton = false;
                }
            } else {
                $scope.showNextHandButton = false;
            }
        } else {
            $scope.showNextHandButton = false;
        }
    }

    $scope.submitMyAction = function(action) {
        $scope.computerGame.myAction = action;

        $http.post('/submitMyAction/', $scope.computerGame).success(function(data) {
            setScopePropertiesCorrect(data);
        })
    }

    $scope.proceedToNextHand = function() {
        $http.post('/proceedToNextHand/', $scope.computerGame).success(function(data) {
            setScopePropertiesCorrect(data);
        })
    }

    function disableActionButtons() {
        if($scope.showNextHandButton == true) {
            $scope.disableFoldButton = true;
            $scope.disableCheckButton = true;
            $scope.disableCallButton = true;
            $scope.disableBetButton = true;
            $scope.disableRaiseButton = true;
        } else if($scope.computerGame.computerTotalBetSize == 0) {
            $scope.disableFoldButton = false;
            $scope.disableCheckButton = false;
            $scope.disableCallButton = true;
            $scope.disableBetButton = false;
            $scope.disableRaiseButton = true;
        } else {
            $scope.disableFoldButton = false;
            $scope.disableCheckButton = true;
            $scope.disableCallButton = false;
            $scope.disableBetButton = true;
            $scope.disableRaiseButton = false;
        }
    }
});