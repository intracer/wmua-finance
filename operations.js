var app = angular.module("app", ["xeditable", "ui.bootstrap", "ui.select", "ngSanitize", "ngMockE2E"]);

app.run(function (editableOptions) {
    editableOptions.theme = 'bs3';
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

app.controller('UiSelectCtrl', function ($scope) {
    $scope.beforeSlash = function (item) {
        return item.text.split("/")[0];
    };
});

app.controller('Ctrl', function ($scope, $filter, $http) {

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

    $scope.showCategory = function (operation) {
        var selected = [];
        if (operation.category_id) {
            selected = $filter('filter')($scope.categories, {value: operation.category_id});
        }
        return selected.length ? selected[0].text : 'Not set';
    };

    $scope.showGrant = function (operation) {
        var selected = [];
        if (operation.grant_id) {
            selected = $filter('filter')($scope.grants, {value: operation.grant_id});
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

    $scope.operations = [
        {
            "id": 1,
            "op_date": "2017-03-17",
            "amount": 3710.00,
            "account_id": 1,
            "category_id": 21,
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
            "category_id": 21,
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
            "category_id": 19,
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

    $scope.categories = [
        {
            value: 1,
            text: "заходи/компенсація витрат на проїзд"
        },
        {
            value: 2,
            text: "заходи/компенсація витрат на проживання"
        },
        {
            value: 3,
            text: "заходи/кава-брейк"
        },
        {
            value: 4,
            text: "заходи/харчування учасників заходів"
        },
        {
            value: 5,
            text: "заходи/друк матеріалів для заходів"
        },
        {
            value: 6,
            text: "заходи/оренда приміщення для заходів"
        },
        {
            value: 7,
            text: "відрядження/транспортні витрати"
        },
        {
            value: 8,
            text: "відрядження/проживання"
        },
        {
            value: 9,
            text: "відрядження/добові"
        },
        {
            value: 10,
            text: "конкурси/Прес-конференція"
        },
        {
            value: 11,
            text: "конкурси/цінні подарунки"
        },
        {
            value: 12,
            text: "конкурси/додаткові сувеніри (журі, оргкомітет)"
        },
        {
            value: 13,
            text: "конкурси/компенсація витрат на проїзд (засідання журі)"
        },
        {
            value: 14,
            text: "конкурси/компенсація витрат на харчування (засідання журі)"
        },
        {
            value: 15,
            text: "конкурси/оформлення приміщення"
        },
        {
            value: 16,
            text: "конкурси/поштові витрати на проект (розсилка запитів, призів)"
        },
        {
            value: 17,
            text: "конкурси/винагорода (робота зі списками)"
        },
        {
            value: 18,
            text: "конкурси/розробка логотипу"
        },
        {
            value: 19,
            text: "адміністративні/банківські витрати"
        },
        {
            value: 20,
            text: "адміністративні/хостинг"
        },
        {
            value: 21,
            text: "адміністративні/реєстрація доменного імені"
        },
        {
            value: 22,
            text: "загальні/видавничі витрати"
        },
        {
            value: 23,
            text: "адміністративні/бухгалтерські послуги"
        },
        {
            value: 24,
            text: "конкурси/витрати на зв'язок"
        },
        {
            value: 25,
            text: "конкурси/транспортні витрати"
        },
        {
            value: 26,
            text: "конкурси/винагорода"
        },
        {
            value: 27,
            text: "загальні/вікісувеніри"
        },
        {
            value: 28,
            text: "загальні/фотокамера"
        },
        {
            value: 29,
            text: "адміністративні/канцтовари"
        },
        {
            value: 30,
            text: "адміністративні/транспортні витрати"
        },
        {
            value: 31,
            text: "адміністративні/операційні витрати"
        },
        {
            value: 32,
            text: "адміністративні/представницькі витрати"
        },
        {
            value: 33,
            text: "відрядження/оформлення документів"
        },
        {
            value: 34,
            text: "оцифрування"
        },
        {
            value: 35,
            text: "адміністративні/оплачуваний працівник"
        },
        {
            value: 36,
            text: "загальні/офісне обладнання"
        },
        {
            value: 37,
            text: "загальні/оренда офісу"
        },
        {
            value: 38,
            text: "заходи/квитки/акредитації на заходи"
        },
        {
            value: 39,
            text: "адміністративні/інтернет"
        },
        {
            value: 40,
            text: "загальні/комунальні"
        },
        {
            value: 41,
            text: "невизначено"
        },
        {
            value: 42,
            text: "адміністративні/офісні потреби"
        },
        {
            value: 43,
            text: "адміністративні/телефон"
        },
        {
            value: 44,
            text: "прибирання"
        }
    ];

    $scope.grants = [
        {
            value: 0,
            text: "власні кошти / кошти на статутну діяльність"
        },
        {
            value: 5,
            text: "волонтерська робота"
        },
        {
            value: 10,
            text: "Grants:PEG/WM UA/Kolessa recordings digitalisation"
        },
        {
            value: 14,
            text: "Grants:PEG/WM UA/Programs in Ukraine 2015-1"
        },
        {
            value: 6,
            text: "Grants:WM UA/Chapter startup"
        },
        {
            value: 9,
            text: "Grants:PEG/WM UA/Wikimedians to the Games"
        },
        {
            value: 13,
            text: "Grants:PEG/WM UA/CEE Meeting 2014"
        },
        {
            value: 2,
            text: "Grants:WM UA/Free Vocal Music concert"
        },
        {
            value: 12,
            text: "Grants:PEG/WM UA/Wiki Loves Monuments 2014"
        },
        {
            value: 7,
            text: "членські внески"
        },
        {
            value: 3,
            text: "Grants:WM UA/Wiki Loves Earth"
        },
        {
            value: 16,
            text: "Grants:PEG/WM UA/Wiki Loves Monuments 2015"
        },
        {
            value: 11,
            text: "Grants:PEG/WM UA/Programs in Ukraine 2014"
        },
        {
            value: 8,
            text: "Grants:PEG/WM UA/Programs in Ukraine 2013"
        },
        {
            value: 4,
            text: "інші цільові внески (партнери)"
        },
        {
            value: 15,
            text: "Grants:PEG/WM UA/Wiki Loves Earth 2015"
        },
        {
            value: 1,
            text: "Grants:WM UA/Programs in Ukraine 2012"
        },
        {
            value: 17,
            text: "Grants:APG/Proposals/2015-2016 round1"
        },
        {
            value: 21,
            text: "Grants:APG/Proposals/2016-2017 round 1"
        }
    ];

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