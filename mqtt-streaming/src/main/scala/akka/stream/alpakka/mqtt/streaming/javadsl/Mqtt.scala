/*
 * Copyright (C) 2016-2019 Lightbend Inc. <http://www.lightbend.com>
 */

package akka.stream.alpakka.mqtt.streaming
package javadsl

import akka.NotUsed
import akka.stream.alpakka.mqtt.streaming.MqttCodec.DecodeError
import akka.stream.javadsl.BidiFlow
import akka.stream.scaladsl.{BidiFlow => ScalaBidiFlow}
import akka.util.ByteString

object Mqtt {

  /**
   * Create a bidirectional flow that maintains client session state with an MQTT endpoint.
   * The bidirectional flow can be joined with an endpoint flow that receives
   * [[ByteString]] payloads and independently produces [[ByteString]] payloads e.g.
   * an MQTT server.
   *
   * @param session the client session to use
   * @param connectionId a identifier to distinguish the client connection so that the session
   *                     can route the incoming requests
   * @return the bidirectional flow
   */
  def clientSessionFlow[A](
      session: MqttClientSession,
      connectionId: ByteString
  ): BidiFlow[Command[A], ByteString, ByteString, DecodeErrorOrEvent[A], NotUsed] =
    inputOutputConverter
      .atop(scaladsl.Mqtt.clientSessionFlow[A](session.underlying, connectionId))
      .asJava

  /**
   * Create a bidirectional flow that maintains client session state with an MQTT endpoint.
   * The bidirectional flow can be joined with an endpoint flow that receives
   * [[ByteString]] payloads and independently produces [[ByteString]] payloads e.g.
   * an MQTT server.
   *
   * @deprecated Provide a connectionId instead (since 1.0.0)
   * @param session the client session to use
   * @return the bidirectional flow
   */
  @Deprecated
  @deprecated("Provide a connectionId instead", "1.0.0")
  def clientSessionFlow[A](
      session: MqttClientSession
  ): BidiFlow[Command[A], ByteString, ByteString, DecodeErrorOrEvent[A], NotUsed] =
    inputOutputConverter
      .atop(scaladsl.Mqtt.clientSessionFlow[A](session.underlying, ByteString("0")))
      .asJava

  /**
   * Create a bidirectional flow that maintains server session state with an MQTT endpoint.
   * The bidirectional flow can be joined with an endpoint flow that receives
   * [[ByteString]] payloads and independently produces [[ByteString]] payloads e.g.
   * an MQTT server.
   *
   * @param session the server session to use
   * @param connectionId a identifier to distinguish the client connection so that the session
   *                     can route the incoming requests
   * @return the bidirectional flow
   */
  def serverSessionFlow[A](
      session: MqttServerSession,
      connectionId: ByteString
  ): BidiFlow[Command[A], ByteString, ByteString, DecodeErrorOrEvent[A], NotUsed] =
    inputOutputConverter
      .atop(scaladsl.Mqtt.serverSessionFlow[A](session.underlying, connectionId))
      .asJava

  /*
   * Converts Java inputs to Scala, and vice-versa.
   */
  private def inputOutputConverter[A] =
    ScalaBidiFlow
      .fromFunctions[Command[A], Command[A], Either[DecodeError, Event[A]], DecodeErrorOrEvent[A]](
        identity,
        DecodeErrorOrEvent.apply
      )
}
