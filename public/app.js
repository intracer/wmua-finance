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
                    templateUrl: 'dictionary.template.html',
                    controller: 'DictionaryController',
                    resolve: {
                        tableName: function(){return 'account'},
                        data: function (dictionaryService) {
                            return dictionaryService.accounts();
                        }
                    }
                })
                .state('categories', {
                    url: '/categories',
                    templateUrl: 'dictionary.template.html',
                    controller: 'DictionaryController',
                    resolve: {
                        tableName: function(){return 'category'},
                        data: function (dictionaryService) {
                            return dictionaryService.categories();
                        }
                    }
                })
                .state('projects', {
                    url: '/projects',
                    templateUrl: 'dictionary.template.html',
                    controller: 'DictionaryController',
                    resolve: {
                        tableName: function(){return 'project'},
                        data: function (dictionaryService) {
                            return dictionaryService.projects();
                        }
                    }
                })
        }
    ]);

financeApp.run(function ($rootScope) {
    $rootScope.$on("$stateChangeError", console.log.bind(console));

//    var $rootScope = angular.element(document.querySelectorAll("[ui-view]")[0]).injector().get('$rootScope');

    $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
        console.log('$stateChangeStart from ' + fromState.name + '- fired when the transition begins. fromState,fromParams : \n', fromState, fromParams);
        console.log('$stateChangeStart to ' + toState.name + '- fired when the transition begins. toState,toParams : \n', toState, toParams);
    });

    $rootScope.$on('$stateChangeError', function (event, toState, toParams, fromState, fromParams) {
        console.log('$stateChangeError - fired when an error occurs during transition.');
        console.log(arguments);
    });

    $rootScope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState, fromParams) {
        console.log('$stateChangeSuccess from ' + toState.name + '- fired once the state transition is complete.');
        console.log('$stateChangeSuccess to ' + toState.name + '- fired once the state transition is complete.');
    });

    $rootScope.$on('$viewContentLoaded', function (event) {
        console.log('$viewContentLoaded - fired after dom rendered', event);
    });

    $rootScope.$on('$stateNotFound', function (event, unfoundState, fromState, fromParams) {
        console.log('$stateNotFound ' + unfoundState.to + '  - fired when a state cannot be found by its name.');
        console.log(unfoundState, fromState, fromParams);
    });
});