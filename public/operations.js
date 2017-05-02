angular.module("financeApp").run(function (editableOptions) {
    editableOptions.theme = 'bs3';
});

angular.module("financeApp").filter('ellipsis', function () {
    return function (text, length) {
        if (text && text.length && text.length > length) {
            return text.substr(0, length) + "...";
        }
        return text;
    }
});

angular.module("financeApp").controller('BsdateCtrl', function ($scope) {
    $scope.operation = {
        op_date: new Date($scope.operation.op_date)
    };

    $scope.opened = {};

    $scope.open = function ($event, elementOpened) {
        $event.preventDefault();
        $event.stopPropagation();

        $scope.opened[elementOpened] = !$scope.opened[elementOpened];
    };
});

angular.module("financeApp").controller('UiSelectCtrl', ['dictionaryService', function ($scope, dictionaryService) {
    $scope.beforeSlash = function (item) {
        return item.text.split("/")[0];
    };

    $scope.grantItemNumber = function (item) {
        var parentNumber = item.number.split('.').slice(0, -1).join('.');
        var parentItem = dictionaryService.grantItems[17]
            .filter(function (item) {
                return item.number == parentNumber;
            })[0];
        var groupName = parentNumber;
        if (parentItem && parentItem.description) {
            groupName += ' ' + parentItem.description;
        }
        return groupName;
    };
}]);


angular.module("financeApp").controller('Ctrl',
['$scope', '$filter', 'NgTableParams', 'Operations', 'dictionaryService', '$window', '$httpParamSerializer',
    function ($scope, $filter, NgTableParams, Operations, dictionaryService, $window, $httpParamSerializer) {
        var vm = this;

        vm.filter = {
            projects: [],
            categories: [],
            grants: [],
            grant_items: [],
            accounts: [],
            dateRange: {
                startDate: moment().subtract(1, 'year').startOf('year'),
                endDate: moment().subtract(1, 'year').endOf('year')
            }
        };

        $scope.filter = vm.filter;

        $scope.projects = [];
        dictionaryService.projects().success(function (data) {$scope.projects = data});

        $scope.categories = [];
        dictionaryService.categories().success(function (data) {$scope.categories = data});

        $scope.accounts = [];
        dictionaryService.accounts().success(function (data) {$scope.accounts = data});

        $scope.grants = dictionaryService.grants;
        $scope.grantItems = dictionaryService.grantItems;

        $scope.grantIdMap = {};

        $scope.operations = [];

        $scope.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            group: "grant_item_name"
        }, {
            dataset: $scope.operations,
            groupOptions: {
                isExpanded: false
            }
        });

        vm.$inject = [/*"NgTableParams", */ "ngTableGroupedList"];

        vm.totalAmount = total($scope.operations, "amount");

        vm.total = total;
        vm.isLastPage = isLastPage;

        $scope.beforeSlash = function (item) {
            return item.text.split("/")[0];
        };

        $scope.grantItemNumber = function (item) {
            var parentNumber = item.number.split('.').slice(0, -1).join('.');
            var parentItem = $scope.grantItems[17]
                .filter(function (item) {
                    return item.number == parentNumber;
                })[0];
            var groupName = parentNumber;
            if (parentItem && parentItem.description) {
                groupName += ' ' + parentItem.description;
            }
            return groupName;
        };

        $scope.opts = {
            ranges: {
                'Last Year': [moment().subtract(1, 'year').startOf('year'), moment().subtract(1, 'year').endOf('year')],
                'This Year': [moment().startOf('year'), moment().endOf('year')]
            }
        };

        $scope.$watch('filter.dateRange', function (newDate) {
            $scope.loadOperations();
        }, false);

        $scope.csvExport = function () {
            var filter = $scope.filter;
            if (filter.dateRange.startDate && filter.dateRange.endDate) {
                var daterange = filter.dateRange.startDate.format("MM/DD/YYYY") + ' - '
                    + filter.dateRange.endDate.format("MM/DD/YYYY")
            }

            var params = {
                projects: filter.projects.join(),
                categories: filter.categories.join(),
                grants: filter.grants.join(),
                grantItems: filter.grant_items.join(),
                accounts: filter.accounts.join(),
                daterange: daterange
            };

            var url = '/csv?' + $httpParamSerializer(params);

            $window.open(url,'_blank')
        };

        $scope.loadOperations = function () {
            Operations.loadOperations(vm.filter).success(function (data) {
                var grantIdMap = {};
                data.forEach(function (op) {
                    grantIdMap[op.id] = op.grant_id;
                });
                grantIdMap['new'] = 17;
                $scope.grantIdMap = grantIdMap;

                $scope.operations = data;
                vm.totalAmount = total($scope.operations, "amount");

                var params = {
                    page: 1,
                    count: 10
                };
                if (window.location.href.indexOf("grouped") > -1) {
                    params['group'] = "grant_item_name";
                }
                $scope.tableParams = new NgTableParams(params, {
                    dataset: data,
                    groupOptions: {
                        isExpanded: false
                    }
                });
            });
        };
        $scope.loadOperations();

        // $scope.grantItems = [{
        //     value: 0,
        //     text: "Nothing"
        // }];
        // vm.grantItems = $scope.grantItems;

        $scope.loadGrantItems = function (grant_id) {
            // $scope.tableParams.reload();
        };

        $scope.grantUpdated = function (item, model, id) {
            $scope.grantIdMap[id] = model;
        };
        vm.grantUpdated = $scope.grantUpdated;

        $scope.showProject = function (operation) {
            var selected = [];
            if (operation.project_id != null) {
                selected = $filter('filter')($scope.projects, {value: operation.project_id}, true);
            }
            return selected.length ? selected[0].text : 'Not set';
        };

        $scope.showCategory = function (operation) {
            var selected = [];
            if (operation.category_id != null) {
                selected = $filter('filter')($scope.categories, {value: operation.category_id}, true);
            }
            return selected.length ? selected[0].text : 'Not set';
        };

        $scope.showGrant = function (operation) {
            var selected = [];
            if (operation.grant_id != null) {
                selected = $filter('filter')($scope.grants, {value: operation.grant_id}, true);
            }
            return selected.length ? selected[0].text : 'Not set';
        };

        $scope.showGrantItem = function (operation) {
            var selected = [];
            if (operation.grant_item_id != null && $scope.grantItems[17].length) {
                selected = $filter('filter')($scope.grantItems[17], {id: operation.grant_item_id}, true);
            }
            return selected.length ? '<i>' + selected[0].number + '</i><br>' + selected[0].description : 'Not set';
        };

        $scope.showAccount = function (operation) {
            var selected = [];
            if (operation.account_id != null) {
                selected = $filter('filter')($scope.accounts, {value: operation.account_id}, true);
            }
            return selected.length ? selected[0].text : 'Not set';
        };

        $scope.saveOperation = Operations.saveOperation;

        // remove operation
        $scope.removeOperation = function (index) {
            $scope.operations.splice(index, 1);
        };

        // add operation
        $scope.addOperation = function () {
            $scope.inserted = {
                id: 'new',
                category_id: 19,
                project_id: 0,
                grant_id: 17,
                grant_item_id: 74,
                account_id: 1,
                amount: "1.5",
                description: "банківські"
            };

            $scope.operations.unshift($scope.inserted);

            $scope.tableParams.sorting({});
            $scope.tableParams.page(1);
            $scope.tableParams = new NgTableParams({
                    page: 1,
                    count: 10
                },
                {dataset: $scope.operations}
            );

            // $scope.tableParams.reload();
        };

        // add operation
        $scope.copyOperation = function (operation) {
            $scope.inserted = {
                id: 'new',
                category_id: operation.category_id,
                project_id: operation.project_id,
                grant_id: operation.grant_id,
                grant_item_id: operation.grant_item_id,
                account_id: operation.account_id,
                amount: operation.amount,
                description: operation.description
            };

            $scope.operations.unshift($scope.inserted);

            $scope.tableParams.sorting({});
            $scope.tableParams.page(1);
            $scope.tableParams = new NgTableParams({
                    page: 1,
                    count: 10
                },
                {dataset: $scope.operations}
            );

            // $scope.tableParams.reload();
        };

        $scope.validateNotEmpty = function (data) {
            var retVal = null;

            if (!data) {
                retVal = "Undefined"
            }
            return retVal;
        };

        function isLastPage() {
            return $scope.tableParams.page() === totalPages();
        }

        function total(data, field) {
            return _.sum(data, field);
        }

        function totalPages() {
            return Math.ceil($scope.tableParams.total() / $scope.tableParams.count());
        }

        $scope.descriptions = [
            "друк", "податки", "нотаріус"
        ];
    }]);