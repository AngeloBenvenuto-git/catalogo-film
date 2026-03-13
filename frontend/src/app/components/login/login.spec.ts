import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router'; // Necessario per simulare il Router nei test

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login, FormsModule], // Aggiungiamo FormsModule qui
      providers: [
        provideRouter([]) // Forniamo una configurazione vuota del router per il test
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Inizializza il componente e rileva i cambiamenti
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
