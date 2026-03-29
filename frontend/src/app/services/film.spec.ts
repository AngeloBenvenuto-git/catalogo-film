import { TestBed } from '@angular/core/testing';

import { FilmService } from './film';

describe('Film', () => {
  let service: FilmService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FilmService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
