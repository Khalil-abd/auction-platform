import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { environment } from '../../../environments/environment';
import { BidPlacedEvent } from '../models';

@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private client: Client;
  private readonly connectionState = new BehaviorSubject<boolean>(false);
  readonly connectionState$ = this.connectionState.asObservable();

  constructor() {
    this.client = new Client({
      webSocketFactory: () => new SockJS(environment.wsUrl) as WebSocket,
      reconnectDelay: 5000,
      onConnect: () => this.connectionState.next(true),
      onDisconnect: () => this.connectionState.next(false),
      onStompError: () => this.connectionState.next(false),
    });
    this.client.activate();
  }

  subscribeToBids(auctionId: string): Observable<BidPlacedEvent> {
    const subject = new Subject<BidPlacedEvent>();

    const doSubscribe = () => {
      this.client.subscribe(`/topic/auction/${auctionId}`, (message: IMessage) => {
        subject.next(JSON.parse(message.body) as BidPlacedEvent);
      });
    };

    if (this.client.connected) {
      doSubscribe();
    } else {
      this.client.onConnect = () => {
        this.connectionState.next(true);
        doSubscribe();
      };
    }

    return subject.asObservable();
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
