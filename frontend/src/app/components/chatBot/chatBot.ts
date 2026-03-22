import { Component, ChangeDetectorRef } from '@angular/core';
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
  messaggio: string = '';
  chatOpen: boolean = false;
  staScrivendo: boolean = false;

  messaggi: { testo: string, daUtente: boolean }[] = [
    { testo: "Ciao! Sono l'assistente NetFilm. Come posso aiutarti?", daUtente: false }
  ];

  constructor(private cdr: ChangeDetectorRef) {}

  async inviaMessaggio() {
    if (!this.messaggio.trim() || this.staScrivendo) return;

    const testoInviato = this.messaggio;
    this.messaggi.push({ testo: testoInviato, daUtente: true });
    this.messaggio = '';
    this.staScrivendo = true;

    // Crea lo spazio per la risposta del bot
    const indiceRisposta = this.messaggi.push({ testo: '', daUtente: false }) - 1;

    try {
      const response = await fetch('http://localhost:8080/api/chat/stream', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: testoInviato })
      });

      if (!response.body) throw new Error("No body");

      const reader = response.body.getReader();
      const decoder = new TextDecoder();

      while (true) {
        const { done, value } = await reader.read();
        if (done) break;

        const chunk = decoder.decode(value, { stream: true });

        // Aggiungi il chunk al testo esistente
        this.messaggi[indiceRisposta].testo += chunk;

        // Forza l'aggiornamento della grafica e scrolla
        this.cdr.detectChanges();
        this.scrollaInBasso();
      }

    } catch (err) {
      console.error("Errore:", err);
      this.messaggi[indiceRisposta].testo = "Errore di connessione.";
    } finally {
      this.staScrivendo = false;
      this.cdr.detectChanges();
    }
  }

  private scrollaInBasso() {
    const chatBody = document.querySelector('.chat-body');
    if (chatBody) {
      chatBody.scrollTop = chatBody.scrollHeight;
    }
  }
}
