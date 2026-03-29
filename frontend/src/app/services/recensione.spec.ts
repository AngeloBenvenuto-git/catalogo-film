import { TestBed } from '@angular/core/testing';
import { RecensioneService } from './recensione.service';
import { provideHttpClient } from '@angular/common/http';

describe('RecensioneService', () => {
  let service: RecensioneService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient()]
    });
    service = TestBed.inject(RecensioneService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
