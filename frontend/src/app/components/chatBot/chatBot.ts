import { Component, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './chatBot.html',
  styleUrl: './chatBot.css'
})
export class ChatBot {
  @ViewChild('chatBody') chatBody!: ElementRef;

  messaggio: string = '';
  chatOpen: boolean = false;
  staScrivendo: boolean = false;
  interrompiScrittura: boolean = false;
  private streamFinito: boolean = false; // <--- Nuova variabile di controllo

  messaggi: { testo: string, daUtente: boolean, ora: Date }[] = [
    { testo: "Ciao! Sono l'assistente NetGPT. Come posso aiutarti?", daUtente: false, ora: new Date() }
  ];

  public codaCaratteri: string[] = [];
  private staElaborandoCoda: boolean = false;
  private velocitaBattitura: number = 20;

  constructor(private cdr: ChangeDetectorRef) {}

  async inviaMessaggio() {
    if (!this.messaggio.trim() || this.staScrivendo) return;

    this.interrompiScrittura = false;
    this.streamFinito = false; // Reset dello stato stream
    const testoInviato = this.messaggio;
    this.messaggi.push({ testo: testoInviato, daUtente: true, ora: new Date() });
    this.messaggio = '';
    this.staScrivendo = true; // Blocca l'input subito

    this.cdr.detectChanges();
    this.scrollaInBasso();

    const indiceRisposta = this.messaggi.push({ testo: '', daUtente: false, ora: new Date() }) - 1;

    try {
      const response = await fetch('http://localhost:8080/api/chat/stream', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: testoInviato })
      });

      if (!response.body) throw new Error("No body");

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      if (!this.staElaborandoCoda) {
        this.elaboraCoda(indiceRisposta);
      }

      while (true) {
        if (this.interrompiScrittura) {
          await reader.cancel();
          break;
        }

        const { done, value } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true });
        let linee = buffer.split('\n');
        buffer = linee.pop() || '';

        for (let linea of linee) {
          if (linea.startsWith('data:')) {
            let contenuto = linea.replace('data:', '');
            contenuto = this.pulisciContenuto(contenuto);
            if (contenuto) {
              for (const char of contenuto) {
                this.codaCaratteri.push(char);
              }
            }
          }
        }
      }
    } catch (err) {
      if (!this.interrompiScrittura) {
        this.messaggi[indiceRisposta].testo = "Errore di connessione.";
      }
    } finally {
      this.streamFinito = true; // Lo stream è finito, ma il bot potrebbe stare ancora scrivendo
      this.cdr.detectChanges();
    }
  }

  private pulisciContenuto(testo: string): string {
    return testo
      .replace(/\\"/g, '"')
      .replace(/\\'/g, "'")
      .replace(/\\\\/g, '')
      .replace(/\\/g, '');
  }

  stopGenerazione() {
    this.interrompiScrittura = true;
    this.codaCaratteri = [];
    this.concludiScrittura();
  }

  private async elaboraCoda(indice: number) {
    this.staElaborandoCoda = true;

    // Il bot continua a scrivere se ci sono caratteri nella coda
    // OPPURE se il server sta ancora inviando (streamFinito è false)
    while (this.codaCaratteri.length > 0 || (!this.streamFinito && !this.interrompiScrittura)) {
      if (this.codaCaratteri.length > 0) {
        const char = this.codaCaratteri.shift();
        if (char) {
          this.messaggi[indice].testo += char;
          this.cdr.detectChanges();
          this.scrollaInBasso();
          await new Promise(resolve => setTimeout(resolve, this.velocitaBattitura));
        }
      } else {
        // Se la coda è vuota ma lo stream non è ancora finito, aspetta un attimo
        await new Promise(resolve => setTimeout(resolve, 30));
      }
    }
    // SOLO QUI, quando tutto è apparso a video, sblocchiamo l'input
    this.concludiScrittura();
  }

  private concludiScrittura() {
    this.staScrivendo = false;
    this.streamFinito = true;
    this.staElaborandoCoda = false;
    this.cdr.detectChanges();
    this.scrollaInBasso();
  }

  // ... restanti funzioni toggleChat e scrollaInBasso uguali
  toggleChat() {
    this.chatOpen = !this.chatOpen;
    if (this.chatOpen) {
      setTimeout(() => this.scrollaInBasso(), 100);
    }
  }

  private scrollaInBasso() {
    setTimeout(() => {
      if (this.chatBody) {
        const el = this.chatBody.nativeElement;
        el.scrollTop = el.scrollHeight;
      }
    }, 0);
  }
}
