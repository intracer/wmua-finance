angular.module("financeApp").service('dictionaryService', ['$http', function ($http) {

    this.insert = function (data) {
        return $http.post('/newdict', data);
    };

    this.update = function (data) {
        return $http.post('/update_dict', data);
    };

    this.projects = [
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

    this.categories = [
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

    this.grants = [
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

    this.accounts = [
        {
            value: 1,
            text: "2202",
            type: "bank"
        },
        {
            value: 2,
            text: "2200",
            type: "bank"
        },
        {
            value: 3,
            text: "2201",
            type: "bank"
        },
        {
            value: 4,
            text: "2203",
            type: "bank"
        },
        {
            value: 5,
            text: "nana",
            type: "cash"
        },
        {
            value: 6,
            text: "ilya",
            type: "cash"
        },
        {
            value: 7,
            text: "sasha",
            type: "cash"
        },
        {
            value: 8,
            text: "anntinomy",
            type: "cash"
        },
        {
            value: 11,
            text: "kharkivian",
            type: "cash"
        },
        {
            value: 12,
            text: "liena",
            type: "cash"
        },
        {
            value: 13,
            text: "base",
            type: "cash"
        },
        {
            value: 14,
            text: "Ata",
            type: "cash"
        },
        {
            value: 15,
            text: "visem",
            type: "cash"
        }
    ];

    this.grantItems = {
        17: [
            {
                "id": 1,
                "grant_id": 17,
                "number": "1.1",
                "description": "Wikimedia Education programme and Wikiworkshops",
                "total_cost": 2000.00
            },
            {
                "id": 2,
                "grant_id": 17,
                "number": "1.2",
                "description": "GLAM outreach",
                "total_cost": 1000.00
            },
            {
                "id": 3,
                "grant_id": 17,
                "number": "1.3",
                "description": "Wikiexpeditions",
                "total_cost": 3000.00
            },
            {
                "id": 4,
                "grant_id": 17,
                "number": "2.1",
                "description": "Article Contests",
                "total_cost": 5000.00
            },
            {
                "id": 5,
                "grant_id": 17,
                "number": "2.1.1",
                "description": "CEE Spring 2016",
                "total_cost": 1050.00
            },
            {
                "id": 6,
                "grant_id": 17,
                "number": "2.1.1.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 7,
                "grant_id": 17,
                "number": "2.1.1.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 150.00
            },
            {
                "id": 8,
                "grant_id": 17,
                "number": "2.1.1.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 9,
                "grant_id": 17,
                "number": "2.1.1.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 10,
                "grant_id": 17,
                "number": "2.1.2",
                "description": "Wiki Loves Monuments article contest",
                "total_cost": 1050.00
            },
            {
                "id": 11,
                "grant_id": 17,
                "number": "2.1.2.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 12,
                "grant_id": 17,
                "number": "2.1.2.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 150.00
            },
            {
                "id": 13,
                "grant_id": 17,
                "number": "2.1.2.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 14,
                "grant_id": 17,
                "number": "2.1.2.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 15,
                "grant_id": 17,
                "number": "2.1.3",
                "description": "Wiki Loves Earth article contest",
                "total_cost": 1050.00
            },
            {
                "id": 16,
                "grant_id": 17,
                "number": "2.1.3.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 17,
                "grant_id": 17,
                "number": "2.1.3.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 150.00
            },
            {
                "id": 18,
                "grant_id": 17,
                "number": "2.1.3.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 19,
                "grant_id": 17,
                "number": "2.1.3.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 20,
                "grant_id": 17,
                "number": "2.1.4",
                "description": "WikiScienceContest",
                "total_cost": 1850.00
            },
            {
                "id": 21,
                "grant_id": 17,
                "number": "2.1.4.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 22,
                "grant_id": 17,
                "number": "2.1.4.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 150.00
            },
            {
                "id": 23,
                "grant_id": 17,
                "number": "2.1.4.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 1250.00
            },
            {
                "id": 24,
                "grant_id": 17,
                "number": "2.1.4.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 25,
                "grant_id": 17,
                "number": "2.1.4.5",
                "description": "Professional jury reimbursements",
                "total_cost": 300.00
            },
            {
                "id": 26,
                "grant_id": 17,
                "number": "2.2",
                "description": "Photo Contests (Wiki Loves Earth International part)",
                "total_cost": 12600.00
            },
            {
                "id": 27,
                "grant_id": 17,
                "number": "2.2.1",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 5000.00
            },
            {
                "id": 28,
                "grant_id": 17,
                "number": "2.2.2",
                "description": "International post expenses",
                "total_cost": 1500.00
            },
            {
                "id": 29,
                "grant_id": 17,
                "number": "2.2.3",
                "description": "Technical support",
                "total_cost": 1500.00
            },
            {
                "id": 30,
                "grant_id": 17,
                "number": "2.2.4",
                "description": "Gifts for international jury",
                "total_cost": 200.00
            },
            {
                "id": 31,
                "grant_id": 17,
                "number": "2.2.5",
                "description": "International presentations (Wikimania, Wikimedia Conference)",
                "total_cost": 1800.00
            },
            {
                "id": 32,
                "grant_id": 17,
                "number": "2.2.6",
                "description": "Publishing WLE calendars",
                "total_cost": 1300.00
            },
            {
                "id": 33,
                "grant_id": 17,
                "number": "2.2.7",
                "description": "Publishing WLE post cards",
                "total_cost": 500.00
            },
            {
                "id": 34,
                "grant_id": 17,
                "number": "2.2.8",
                "description": "Support to local organising teams",
                "total_cost": 800.00
            },
            {
                "id": 35,
                "grant_id": 17,
                "number": "2.3",
                "description": "Photo Contests (National)",
                "total_cost": 13200.00
            },
            {
                "id": 36,
                "grant_id": 17,
                "number": "2.3.1",
                "description": "Wiki Loves Earth in Ukraine",
                "total_cost": 5500.00
            },
            {
                "id": 37,
                "grant_id": 17,
                "number": "2.3.1.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 150.00
            },
            {
                "id": 46,
                "grant_id": 17,
                "number": "2.3.1.10",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 500.00
            },
            {
                "id": 38,
                "grant_id": 17,
                "number": "2.3.1.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 150.00
            },
            {
                "id": 39,
                "grant_id": 17,
                "number": "2.3.1.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 500.00
            },
            {
                "id": 40,
                "grant_id": 17,
                "number": "2.3.1.4",
                "description": "Main Prizes (incl. diplomas)",
                "total_cost": 1600.00
            },
            {
                "id": 41,
                "grant_id": 17,
                "number": "2.3.1.5",
                "description": "Medium Prizes (incl. diplomas)",
                "total_cost": 1000.00
            },
            {
                "id": 42,
                "grant_id": 17,
                "number": "2.3.1.6",
                "description": "Small Prizes (incl. diplomas)",
                "total_cost": 700.00
            },
            {
                "id": 43,
                "grant_id": 17,
                "number": "2.3.1.7",
                "description": "Regional Prizes & Awards (incl. diplomas)",
                "total_cost": 500.00
            },
            {
                "id": 44,
                "grant_id": 17,
                "number": "2.3.1.8",
                "description": "Special nominations prizes (incl. diplomas)",
                "total_cost": 300.00
            },
            {
                "id": 45,
                "grant_id": 17,
                "number": "2.3.1.9",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 47,
                "grant_id": 17,
                "number": "2.3.2",
                "description": "Wiki Loves Monuments in Ukraine",
                "total_cost": 5575.00
            },
            {
                "id": 48,
                "grant_id": 17,
                "number": "2.3.2.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 150.00
            },
            {
                "id": 57,
                "grant_id": 17,
                "number": "2.3.2.10",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 600.00
            },
            {
                "id": 49,
                "grant_id": 17,
                "number": "2.3.2.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 125.00
            },
            {
                "id": 50,
                "grant_id": 17,
                "number": "2.3.2.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 500.00
            },
            {
                "id": 51,
                "grant_id": 17,
                "number": "2.3.2.4",
                "description": "Main Prizes (incl. diplomas)",
                "total_cost": 1600.00
            },
            {
                "id": 52,
                "grant_id": 17,
                "number": "2.3.2.5",
                "description": "Medium Prizes (incl. diplomas)",
                "total_cost": 1000.00
            },
            {
                "id": 53,
                "grant_id": 17,
                "number": "2.3.2.6",
                "description": "Small Prizes (incl. diplomas)",
                "total_cost": 700.00
            },
            {
                "id": 54,
                "grant_id": 17,
                "number": "2.3.2.7",
                "description": "Regional Prizes & Awards (incl. diplomas)",
                "total_cost": 500.00
            },
            {
                "id": 55,
                "grant_id": 17,
                "number": "2.3.2.8",
                "description": "Special nominations prizes (incl. diplomas)",
                "total_cost": 300.00
            },
            {
                "id": 56,
                "grant_id": 17,
                "number": "2.3.2.9",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 58,
                "grant_id": 17,
                "number": "2.3.3",
                "description": "European Science Photo Competition in Ukraine",
                "total_cost": 2125.00
            },
            {
                "id": 59,
                "grant_id": 17,
                "number": "2.3.3.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 60,
                "grant_id": 17,
                "number": "2.3.3.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 125.00
            },
            {
                "id": 61,
                "grant_id": 17,
                "number": "2.3.3.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 300.00
            },
            {
                "id": 62,
                "grant_id": 17,
                "number": "2.3.3.4",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 1200.00
            },
            {
                "id": 63,
                "grant_id": 17,
                "number": "2.3.3.5",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 64,
                "grant_id": 17,
                "number": "2.3.3.6",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 300.00
            },
            {
                "id": 65,
                "grant_id": 17,
                "number": "2.4",
                "description": "Thematic (collaboration) weeks and months",
                "total_cost": 1000.00
            },
            {
                "id": 66,
                "grant_id": 17,
                "number": "2.4.1",
                "description": "Organising local offline edit-a-thons",
                "total_cost": 500.00
            },
            {
                "id": 67,
                "grant_id": 17,
                "number": "2.4.2",
                "description": "Small gifts for international weeks (e.g. Asian Month, Ukrainian-Armenian week etc.)",
                "total_cost": 400.00
            },
            {
                "id": 68,
                "grant_id": 17,
                "number": "2.4.3",
                "description": "Presentation events for regional weeks (e.g. Luhansk Oblast thematic week)",
                "total_cost": 100.00
            },
            {
                "id": 69,
                "grant_id": 17,
                "number": "3.1",
                "description": "Publishing and Souvenirs",
                "total_cost": 7000.00
            },
            {
                "id": 70,
                "grant_id": 17,
                "number": "3.2",
                "description": "Microgrants",
                "total_cost": 3000.00
            },
            {
                "id": 71,
                "grant_id": 17,
                "number": "3.3",
                "description": "Scholarships",
                "total_cost": 5000.00
            },
            {
                "id": 72,
                "grant_id": 17,
                "number": "3.4",
                "description": "Community Events (WikiConference, General Meeting etc.)",
                "total_cost": 5000.00
            },
            {
                "id": 73,
                "grant_id": 17,
                "number": "3.5",
                "description": "Trainings for Volunteers",
                "total_cost": 1000.00
            },
            {
                "id": 77,
                "grant_id": 17,
                "number": "4.0",
                "description": "N/A",
                "total_cost": 0.00
            },
            {
                "id": 74,
                "grant_id": 17,
                "number": "4.1",
                "description": "Operations (excludes staff and programs)",
                "total_cost": 7368.00
            },
            {
                "id": 75,
                "grant_id": 17,
                "number": "4.2",
                "description": "Staff",
                "total_cost": 10032.00
            }
        ],
        21: [
            {
                "id": 149,
                "grant_id": 21,
                "number": "1.1",
                "description": "Wikipedia Education Programme & Wikiworkshops",
                "total_cost": 6000.00
            },
            {
                "id": 150,
                "grant_id": 21,
                "number": "1.2",
                "description": "GLAM (БоГеМА)",
                "total_cost": 1500.00
            },
            {
                "id": 151,
                "grant_id": 21,
                "number": "1.3",
                "description": "Promoting Wikimedia and Free licenses",
                "total_cost": 1000.00
            },
            {
                "id": 152,
                "grant_id": 21,
                "number": "2.1",
                "description": "Article Contests",
                "total_cost": 4000.00
            },
            {
                "id": 153,
                "grant_id": 21,
                "number": "2.1.1",
                "description": "CEE Spring 2016",
                "total_cost": 1000.00
            },
            {
                "id": 154,
                "grant_id": 21,
                "number": "2.1.1.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 155,
                "grant_id": 21,
                "number": "2.1.1.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 100.00
            },
            {
                "id": 156,
                "grant_id": 21,
                "number": "2.1.1.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 157,
                "grant_id": 21,
                "number": "2.1.1.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 158,
                "grant_id": 21,
                "number": "2.1.2",
                "description": "Wiki Loves Monuments article contest",
                "total_cost": 1000.00
            },
            {
                "id": 159,
                "grant_id": 21,
                "number": "2.1.2.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 160,
                "grant_id": 21,
                "number": "2.1.2.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 100.00
            },
            {
                "id": 161,
                "grant_id": 21,
                "number": "2.1.2.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 162,
                "grant_id": 21,
                "number": "2.1.2.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 163,
                "grant_id": 21,
                "number": "2.1.3",
                "description": "Wiki Loves Earth article contest",
                "total_cost": 1000.00
            },
            {
                "id": 164,
                "grant_id": 21,
                "number": "2.1.3.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 165,
                "grant_id": 21,
                "number": "2.1.3.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 100.00
            },
            {
                "id": 166,
                "grant_id": 21,
                "number": "2.1.3.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 167,
                "grant_id": 21,
                "number": "2.1.3.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 168,
                "grant_id": 21,
                "number": "2.1.4",
                "description": "WikiScienceContest",
                "total_cost": 1000.00
            },
            {
                "id": 169,
                "grant_id": 21,
                "number": "2.1.4.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 170,
                "grant_id": 21,
                "number": "2.1.4.2",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 100.00
            },
            {
                "id": 171,
                "grant_id": 21,
                "number": "2.1.4.3",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 750.00
            },
            {
                "id": 172,
                "grant_id": 21,
                "number": "2.1.4.4",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 50.00
            },
            {
                "id": 173,
                "grant_id": 21,
                "number": "2.2",
                "description": "Photo Contests (Wiki Loves Earth International part)",
                "total_cost": 12600.00
            },
            {
                "id": 174,
                "grant_id": 21,
                "number": "2.2.1",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 5000.00
            },
            {
                "id": 175,
                "grant_id": 21,
                "number": "2.2.2",
                "description": "International post expenses",
                "total_cost": 1500.00
            },
            {
                "id": 176,
                "grant_id": 21,
                "number": "2.2.3",
                "description": "Technical support",
                "total_cost": 1500.00
            },
            {
                "id": 177,
                "grant_id": 21,
                "number": "2.2.4",
                "description": "Gifts for international jury",
                "total_cost": 200.00
            },
            {
                "id": 178,
                "grant_id": 21,
                "number": "2.2.5",
                "description": "International presentations (Wikimania, Wikimedia Conference)",
                "total_cost": 1800.00
            },
            {
                "id": 179,
                "grant_id": 21,
                "number": "2.2.6",
                "description": "Publishing WLE calendars",
                "total_cost": 1300.00
            },
            {
                "id": 180,
                "grant_id": 21,
                "number": "2.2.7",
                "description": "Publishing WLE post cards",
                "total_cost": 500.00
            },
            {
                "id": 181,
                "grant_id": 21,
                "number": "2.2.8",
                "description": "Support to local organising teams",
                "total_cost": 800.00
            },
            {
                "id": 182,
                "grant_id": 21,
                "number": "2.3",
                "description": "Photo Contests (National)",
                "total_cost": 13100.00
            },
            {
                "id": 183,
                "grant_id": 21,
                "number": "2.3.1",
                "description": "Wiki Loves Earth in Ukraine",
                "total_cost": 5500.00
            },
            {
                "id": 184,
                "grant_id": 21,
                "number": "2.3.1.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 150.00
            },
            {
                "id": 185,
                "grant_id": 21,
                "number": "2.3.1.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 150.00
            },
            {
                "id": 186,
                "grant_id": 21,
                "number": "2.3.1.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 500.00
            },
            {
                "id": 187,
                "grant_id": 21,
                "number": "2.3.1.4",
                "description": "Main Prizes (incl. diplomas)",
                "total_cost": 1600.00
            },
            {
                "id": 188,
                "grant_id": 21,
                "number": "2.3.1.5",
                "description": "Medium Prizes (incl. diplomas)",
                "total_cost": 1000.00
            },
            {
                "id": 189,
                "grant_id": 21,
                "number": "2.3.1.6",
                "description": "Small Prizes (incl. diplomas)",
                "total_cost": 700.00
            },
            {
                "id": 190,
                "grant_id": 21,
                "number": "2.3.1.7",
                "description": "Regional Prizes & Awards (incl. diplomas)",
                "total_cost": 500.00
            },
            {
                "id": 191,
                "grant_id": 21,
                "number": "2.3.1.8",
                "description": "Special nominations prizes (incl. diplomas)",
                "total_cost": 300.00
            },
            {
                "id": 192,
                "grant_id": 21,
                "number": "2.3.1.9",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 193,
                "grant_id": 21,
                "number": "2.3.1.10",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 500.00
            },
            {
                "id": 194,
                "grant_id": 21,
                "number": "2.3.2",
                "description": "Wiki Loves Monuments in Ukraine",
                "total_cost": 5575.00
            },
            {
                "id": 195,
                "grant_id": 21,
                "number": "2.3.2.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 150.00
            },
            {
                "id": 196,
                "grant_id": 21,
                "number": "2.3.2.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 125.00
            },
            {
                "id": 197,
                "grant_id": 21,
                "number": "2.3.2.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 500.00
            },
            {
                "id": 198,
                "grant_id": 21,
                "number": "2.3.2.4",
                "description": "Main Prizes (incl. diplomas)",
                "total_cost": 1600.00
            },
            {
                "id": 199,
                "grant_id": 21,
                "number": "2.3.2.5",
                "description": "Medium Prizes (incl. diplomas)",
                "total_cost": 1000.00
            },
            {
                "id": 200,
                "grant_id": 21,
                "number": "2.3.2.6",
                "description": "Small Prizes (incl. diplomas)",
                "total_cost": 700.00
            },
            {
                "id": 201,
                "grant_id": 21,
                "number": "2.3.2.7",
                "description": "Regional Prizes & Awards (incl. diplomas)",
                "total_cost": 500.00
            },
            {
                "id": 202,
                "grant_id": 21,
                "number": "2.3.2.8",
                "description": "Special nominations prizes (incl. diplomas)",
                "total_cost": 300.00
            },
            {
                "id": 203,
                "grant_id": 21,
                "number": "2.3.2.9",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 204,
                "grant_id": 21,
                "number": "2.3.2.10",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 600.00
            },
            {
                "id": 205,
                "grant_id": 21,
                "number": "2.3.3",
                "description": "European Science Photo Competition in Ukraine",
                "total_cost": 2025.00
            },
            {
                "id": 206,
                "grant_id": 21,
                "number": "2.3.3.1",
                "description": "Hall rent for Awards Ceremony & Press event",
                "total_cost": 100.00
            },
            {
                "id": 207,
                "grant_id": 21,
                "number": "2.3.3.2",
                "description": "Hall decorations (printing photos)",
                "total_cost": 125.00
            },
            {
                "id": 208,
                "grant_id": 21,
                "number": "2.3.3.3",
                "description": "Travel costs (jury members, participants)",
                "total_cost": 200.00
            },
            {
                "id": 209,
                "grant_id": 21,
                "number": "2.3.3.4",
                "description": "Prizes (incl. diplomas)",
                "total_cost": 1200.00
            },
            {
                "id": 210,
                "grant_id": 21,
                "number": "2.3.3.5",
                "description": "Food & beverages (coffee break or stand-up party)",
                "total_cost": 100.00
            },
            {
                "id": 211,
                "grant_id": 21,
                "number": "2.3.3.6",
                "description": "Local presentation events (press conference, exhibitions etc.)",
                "total_cost": 300.00
            },
            {
                "id": 212,
                "grant_id": 21,
                "number": "2.4",
                "description": "Wikiexpeditions",
                "total_cost": 3000.00
            },
            {
                "id": 213,
                "grant_id": 21,
                "number": "3.1",
                "description": "Publishing and Souvenirs",
                "total_cost": 7000.00
            },
            {
                "id": 214,
                "grant_id": 21,
                "number": "3.2",
                "description": "Microgrants",
                "total_cost": 3000.00
            },
            {
                "id": 215,
                "grant_id": 21,
                "number": "3.3",
                "description": "Scholarships",
                "total_cost": 8000.00
            },
            {
                "id": 216,
                "grant_id": 21,
                "number": "3.4",
                "description": "Community Events (WikiConference, General Meeting etc.)",
                "total_cost": 5000.00
            },
            {
                "id": 217,
                "grant_id": 21,
                "number": "3.5",
                "description": "Trainings for Volunteers",
                "total_cost": 1000.00
            },
            {
                "id": 218,
                "grant_id": 21,
                "number": "4.1",
                "description": "Operations (excludes staff and programs)",
                "total_cost": 7952.00
            },
            {
                "id": 219,
                "grant_id": 21,
                "number": "4.2",
                "description": "Staff",
                "total_cost": 15048.00
            }

        ]
    };

}]);