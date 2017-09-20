package io.sirmata

package object protocol
    extends protocol.request.Commands
    with protocol.request.Serializer
    with protocol.CommandsResponse {

  val MinCommandSize = 3
}
  