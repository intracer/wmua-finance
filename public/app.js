var financeApp = angular.module("financeApp",
    ["xeditable", "ui.bootstrap", "ui.select",
        'mwl.confirm', "ngSanitize", "ngTable", "daterangepicker", 'ui.router'])
    .config(['$stateProvider', '$urlRouterProvider',
        function config($stateProvider, $urlRouterProvider) {

            $urlRouterProvider.otherwise('/operations');

            $stateProvider
                .state('operations', {
                    url: '/operations',
                    templateUrl: 'operations.template.html'
                })
                .state('grouped', {
                    url: '/grouped',
                    templateUrl: 'grouped.template.html'
                })
                .state('accounts', {
                    url: '/accounts',
                    templateUrl: 'dictionary.template.html'
                })
                .state('categories', {
                    url: '/categories',
                    templateUrl: 'dictionary.template.html'
                })
                .state('projects', {
                    url: '/projects',
                    templateUrl: 'dictionary.template.html'
                })
        }
    ]);