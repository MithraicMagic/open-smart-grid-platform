export class ProtocolInfo {
  constructor(
    public protocol: string,
    public protocolVersion: string,
    public outgoingRequestsPropertyPrefix: string,
    public incomingResponsesPropertyPrefix: string,
    public incomingRequestsPropertyPrefix: string,
    public outgoingResponsesPropertyPrefix: string,
    public parallelRequestsAllowed: boolean
  ) {
  }
}
