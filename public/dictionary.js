angular.module("financeApp").controller('DictionaryController',
    ['tableName', 'data', '$scope', 'dictionaryService', 'NgTableParams',
        function(tableName, data, $scope, dictionaryService, NgTableParams) {
    $scope.data = data;

    $scope.tableParams = new NgTableParams({
        page: 1,
        count: 15
    }, {
        dataset: data
    });

    var addRow = function (row) {
        $scope.data.unshift(row);

        $scope.tableParams.sorting({});
        $scope.tableParams = new NgTableParams({
                page: 1,
                count: 15
            },
            {dataset: $scope.data}
        );
    };

    $scope.addLookup = function () {
        $scope.inserted = {
            value: 'new',
            text: ""
        };

        addRow($scope.inserted)
    };

    $scope.copyLookup = function (lookup) {
        $scope.inserted = {
            id: 'new',
            text: lookup.text
        };

        addRow($scope.inserted)
    };

    $scope.saveOrUpdateLookup = function (data, lookup) {
        if (lookup.value == 'new') {
            lookup.value = null;

            var toInsert = {
                table: tableName,
                value: data.text
            };

            return dictionaryService.insert(toInsert);
        } else {
            var toUpdate = {
                pk: lookup.value,
                name: 'name',
                table: tableName,
                value: data.text
            };

            return dictionaryService.update(toUpdate);
        }
    };
}]);

