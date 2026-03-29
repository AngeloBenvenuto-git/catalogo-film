import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessaggioService } from '../../services/messaggio.service';

@Component({
  selector: 'app-contattaci',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './contattaci.component.html',
  styleUrls: ['./contattaci.component.css']
})
export class ContattaciComponent {
  oggetto: string = '';
  testo: string = '';

  showToast: boolean = false;
  toastMessage: string = '';
  isError: boolean = false;
  isSubmitting: boolean = false;

  constructor(
    private messaggioService: MessaggioService,
    private cdr: ChangeDetectorRef
  ) {}

  inviaMessaggio() {
    if (!this.oggetto.trim() || !this.testo.trim()) {
      this.triggerToast('Compila tutti i campi prima di inviare.', true);
      return;
    }

    this.isSubmitting = true;
    this.cdr.detectChanges();

    this.messaggioService.inviaMessaggio(this.oggetto, this.testo).subscribe({
      next: () => {
        this.triggerToast('Messaggio inviato con successo! Ti risponderemo presto.', false);
        this.oggetto = '';
        this.testo = '';
        this.isSubmitting = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.triggerToast('Errore durante l\'invio. Riprova più tardi.', true);
        this.isSubmitting = false;
        this.cdr.detectChanges();
      }
    });
  }

  triggerToast(message: string, error: boolean) {
    this.toastMessage = message;
    this.isError = error;
    this.showToast = true;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.showToast = false;
      this.cdr.detectChanges();
    }, 3000);
  }
}
