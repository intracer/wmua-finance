'use strict';

describe('Ctrl', function() {

    beforeEach(module('financeApp'));

    it('should create a `Ctrl` model with 3 phones', inject(function($controller) {
        var scope = {};
        var ctrl = $controller('Ctrl', {$scope: scope});

        expect(ctrl.filter_projects).toBe([]);
        expect(ctrl.filter_categories).toBe([]);
        expect(ctrl.filter_grants).toBe([]);
        expect(ctrl.filter_grant_items).toBe([]);
        expect(ctrl.filter_accounts).toBe([]);
    }));

});
