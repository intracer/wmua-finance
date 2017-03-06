angular.module("financeApp").controller('DictionaryController',
    ['tableName', 'data', '$scope', 'dictionaryService', 'NgTableParams',
        function (tableName, data, $scope, dictionaryService, NgTableParams) {

            function setData(data) {
                $scope.data = data;
                $scope.tableParams = new NgTableParams({
                        page: 1,
                        count: 15
                    },
                    {dataset: data}
                );
            }
            setData(data.data);

            var addRow = function (row) {
                $scope.data.unshift(row);
                setData($scope.data);
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

