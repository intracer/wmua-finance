angular.module("financeApp").controller('DictionaryController', ['data', '$scope', 'NgTableParams', function(data, $scope, NgTableParams) {
    $scope.data = data;

    $scope.tableParams = new NgTableParams({
        page: 1,
        count: 30
    }, {
        dataset: data
    });

}]);

