import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MessaggioService } from '../../services/messaggio.service';

@Component({
  selector: 'app-admin-messaggi',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [DatePipe],
  templateUrl: './admin-messaggi.component.html',
  styleUrls: ['./admin-messaggi.component.css']
})
export class AdminMessaggiComponent implements OnInit {
  messaggi: any[] = [];
  messaggioSelezionato: any = null;
  rispostaTesto: string = '';

  constructor(
    private messaggioService: MessaggioService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.caricaMessaggi();
  }

  caricaMessaggi() {
    this.messaggioService.getTuttiMessaggi().subscribe({
      next: (data) => {
        this.messaggi = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }

  apriMessaggio(msg: any) {
    this.messaggioSelezionato = msg;
    this.rispostaTesto = '';
    this.cdr.detectChanges();

    if (!msg.letto) {
      this.messaggioService.segnaComeLetto(msg.id).subscribe({
        next: () => {
          msg.letto = true;
          this.cdr.detectChanges();
        }
      });
    }
  }

  inviaRisposta(id: number) {
    if (!this.rispostaTesto.trim()) return;

    this.messaggioService.rispondiAlMessaggio(id, this.rispostaTesto).subscribe({
      next: (res) => {
        this.messaggioSelezionato.risposta = res.risposta;
        this.messaggioSelezionato.dataRisposta = res.dataRisposta;
        this.rispostaTesto = '';

        const index = this.messaggi.findIndex(m => m.id === id);
        if (index !== -1) {
          this.messaggi[index].risposta = res.risposta;
          this.messaggi[index].dataRisposta = res.dataRisposta;
        }

        this.cdr.detectChanges();
      }
    });
  }

  chiudiModale() {
    this.messaggioSelezionato = null;
    this.cdr.detectChanges();
  }

  eliminaMessaggio(id: number) {
    if(confirm('Sei sicuro di voler eliminare questo messaggio?')) {
      this.messaggioService.cancellaMessaggio(id).subscribe({
        next: () => {
          this.caricaMessaggi();
        }
      });
    }
  }
}
