'use strict';

describe('Ctrl', function() {

    beforeEach(module('financeApp'));

    it('should create a `Ctrl` model', inject(function($controller) {
        var scope = {};
        var ctrl = $controller('Ctrl', {$scope: scope});

        expect(ctrl.filter.projects).toBe([]);
        expect(ctrl.filter.categories).toBe([]);
        expect(ctrl.filter.grants).toBe([]);
        expect(ctrl.filter.grant_items).toBe([]);
        expect(ctrl.filter.accounts).toBe([]);
    }));

});
