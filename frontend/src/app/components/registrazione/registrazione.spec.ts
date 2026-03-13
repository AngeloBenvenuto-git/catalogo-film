import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Registrazione } from './registrazione';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

describe('Registrazione', () => {
  let component: Registrazione;
  let fixture: ComponentFixture<Registrazione>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Registrazione, FormsModule],
      providers: [provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(Registrazione);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
