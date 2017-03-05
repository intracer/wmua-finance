var financeApp = angular.module("financeApp",
    ["xeditable", "ui.bootstrap", "ui.select",
        'mwl.confirm', "ngSanitize", "ngTable", "daterangepicker", 'ngRoute']).config(['$locationProvider', '$routeProvider',
    function config($locationProvider, $routeProvider) {
        $locationProvider.hashPrefix('!');

        $routeProvider
            .when('/operations', {
                templateUrl: 'operations.template.html'
            })
            .when('/grouped', {
                templateUrl: 'grouped.template.html'
            })
            .when('/accounts', {
                templateUrl: 'dictionary.template.html'
            })
            .when('/categories', {
                templateUrl: 'dictionary.template.html'
            })
            .when('/projects', {
                templateUrl: 'dictionary.template.html'
            })
            .otherwise('/operations');
    }
]);