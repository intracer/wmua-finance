var app = angular.module("app", ["xeditable", "ui.bootstrap", "ngMockE2E"]);

app.run(function (editableOptions) {
    editableOptions.theme = 'bs3';
});

app.controller('Ctrl', function ($scope, $filter, $http) {
    $scope.operations = [
        {
            "id": 1,
            "op_date": "2017-03-17",
            "amount": 3710.00,
            "account_id": 1,
            "cat_id": 21,
            "project_id": 1,
            "grant_id": 8,
            "description": "друк",
            "grant_row": null,
            "grant_item": 69,
            "log_date": "2017-02-12",
            "user_id": 1,
            "parent_rev_id": null,
            "op_id": 1
        },
        {
            "id": 2,
            "op_date": "2017-03-17",
            "amount": 987.34,
            "account_id": 1,
            "cat_id": 21,
            "project_id": 1,
            "grant_id": 8,
            "description": "податки",
            "grant_row": null,
            "grant_item": null,
            "log_date": "2017-02-12 16:43:24",
            "user_id": 1,
            "parent_rev_id": null,
            "op_id": 2
        },
        {
            "id": 3,
            "op_date": "2017-04-17T22:00:00.000Z",
            "amount": 65.00,
            "account_id": 1,
            "cat_id": 19,
            "project_id": 2,
            "grant_id": 8,
            "description": "нотаріус",
            "grant_row": null,
            "grant_item": null,
            "log_date": "2017-02-12 16:43:24",
            "user_id": 1,
            "parent_rev_id": null,
            "op_id": 3
        }];

    $scope.projects = [        
        {
            value: 0,
            text: "адміністрування"
        },
        {
            value: 1,
            text: "ВЛЗ"
        },
        {
            value: 2,
            text: "вікіконцерт"
        },
        {
            value: 3,
            text: "віківишколи"
        },
        {
            value: 4,
            text: "вікіконференція"
        },
        {
            value: 5,
            text: "видавничі витрати"
        },
        {
            value: 6,
            text: "ВЛП"
        },
        {
            value: 7,
            text: "вікіекспедиції"
        },
        {
            value: 8,
            text: "ЗЗ 2011"
        },
        {
            value: 9,
            text: "вікісувеніри"
        },
        {
            value: 10,
            text: "альбом ВЛП"
        },
        {
            value: 11,
            text: "фотокамера"
        },
        {
            value: 12,
            text: "закарпаття"
        },
        {
            value: 13,
            text: "французька осінь"
        },
        {
            value: 14,
            text: "благодійність"
        },
        {
            value: 15,
            text: "вікізгущівки"
        },
        {
            value: 16,
            text: "СОКІЛ"
        },
        {
            value: 17,
            text: "ЗЗ 2012"
        },
        {
            value: 18,
            text: "вікіекспедиції 2011"
        },
        {
            value: 19,
            text: "колесса"
        },
        {
            value: 20,
            text: "ЗЗ 2013"
        },
        {
            value: 21,
            text: "Вікіконференція-2013"
        },
        {
            value: 22,
            text: "Вірменія"
        },
        {
            value: 23,
            text: "бібліотека"
        },
        {
            value: 24,
            text: "Галерея слави"
        },
        {
            value: 25,
            text: "Сочі"
        },
        {
            value: 26,
            text: "Офіс"
        },
        {
            value: 27,
            text: "Вікіфлешмоб ім. Костенка"
        },
        {
            value: 28,
            text: "НАТО"
        },
        {
            value: 29,
            text: "Фотографування на заходах"
        },
        {
            value: 30,
            text: "CEE Meeting"
        },
        {
            value: 31,
            text: "WLE international"
        },
        {
            value: 32,
            text: "Військова справа"
        },
        {
            value: 33,
            text: "СКАНЕР"
        },
        {
            value: 34,
            text: "ЗЗ 2014 позачергові"
        },
        {
            value: 35,
            text: "Вікіпедія любить памятки"
        },
        {
            value: 36,
            text: "ВЛП 2014"
        },
        {
            value: 37,
            text: "Стипендії іноземні заходи"
        },
        {
            value: 38,
            text: "Науковий фотоконкурс"
        },
        {
            value: 39,
            text: "Тренінги"
        },
        {
            value: 40,
            text: "невизначено"
        },
        {
            value: 42,
            text: "15 річчя ВП"
        }
    ];
    
    $scope.groups = [];
    $scope.loadGroups = function () {
        return $scope.groups.length ? null : $http.get('/groups').success(function (data) {
                $scope.groups = data;
            });
    };

    $scope.showGroup = function (user) {
        if (user.group && $scope.groups.length) {
            var selected = $filter('filter')($scope.groups, {id: user.group});
            return selected.length ? selected[0].text : 'Not set';
        } else {
            return user.groupName || 'Not set';
        }
    };

    $scope.showProject = function (operation) {
        var selected = [];
        if (operation.project_id) {
            selected = $filter('filter')($scope.projects, {value: operation.project_id});
        }
        return selected.length ? selected[0].text : 'Not set';
    };

    $scope.saveOperation = function (data, id) {
        //$scope.user not updated yet
        angular.extend(data, {id: id});
        return $http.post('/saveOperation', data);
    };

    // remove user
    $scope.removeOperation = function (index) {
        $scope.operations.splice(index, 1);
    };

    // add user
    $scope.addOperation = function () {
        $scope.inserted = {
            id: $scope.operations.length + 1,
            name: '',
            status: null,
            group: null
        };
        $scope.operations.push($scope.inserted);
    };
});


app.controller('BsdateCtrl', function ($scope) {
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

// --------------- mock $http requests ----------------------
app.run(function ($httpBackend) {
    $httpBackend.whenGET('/groups').respond([
        {id: 1, text: 'user'},
        {id: 2, text: 'customer'},
        {id: 3, text: 'vip'},
        {id: 4, text: 'admin'}
    ]);

    $httpBackend.whenPOST(/\/saveOperation/).respond(function (method, url, data) {
        data = angular.fromJson(data);
        return [200, {status: 'ok'}];
    });
});