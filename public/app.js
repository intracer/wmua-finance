var financeApp = angular.module("financeApp",
    ["xeditable", "ui.bootstrap", "ui.select",
        'mwl.confirm', "ngSanitize", "ngTable", "daterangepicker", 'ngRoute']).config(['$locationProvider', '$routeProvider',
    function config($locationProvider, $routeProvider) {
        $locationProvider.hashPrefix('!');

        $routeProvider.when('/operations', {
            templateUrl: 'operations.template.html'
        }).when('/grouped', {
            templateUrl: 'grouped.template.html'
        }).otherwise('/operations');
    }
]);