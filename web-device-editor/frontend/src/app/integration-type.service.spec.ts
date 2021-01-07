import { TestBed } from '@angular/core/testing';

import { IntegrationTypeService } from './integration-type.service';

describe('IntegrationTypeService', () => {
  let service: IntegrationTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IntegrationTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
