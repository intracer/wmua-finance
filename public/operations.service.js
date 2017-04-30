angular.module("financeApp").service('Operations', [ '$http', function($http) {
    this.saveOperation = function (data, operation) {
        var momentDate = moment(data.date);
        if (!momentDate.isValid()) {
            return "Undefined date"
        }
        var dateStr = momentDate.format('YYYY-MM-DD[T]HH:mm:ss.SSSZZ').slice(0, 10);
        if (operation.id == 'new') {
            operation.id = null
        }
        angular.extend(data, {
            id: operation.id,
            op_id: operation.op_id,
            date: dateStr
        });
        return $http.post('/newoperation', data);
    };

    this.loadOperations = function (filter) {
        if (filter.dateRange.startDate && filter.dateRange.endDate) {
            var daterange = filter.dateRange.startDate.format("MM/DD/YYYY") + ' - '
                + filter.dateRange.endDate.format("MM/DD/YYYY")
        }
        return $http({
            url: '/operations_ws',
            method: "GET",
            params: {
                projects: filter.projects.join(),
                categories: filter.categories.join(),
                grants: filter.grants.join(),
                grantItems: filter.grant_items.join(),
                accounts: filter.accounts.join(),
                daterange: daterange
            }
        })
    };

}]);