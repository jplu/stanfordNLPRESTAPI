/**
 * This file is part of StanfordNLPRESTAPI.
 *
 * StanfordNLPRESTAPI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * StanfordNLPRESTAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with StanfordNLPRESTAPI.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.eurecom.stanfordnlprestapi.exceptions;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import java.lang.reflect.Method;
import java.nio.charset.Charset;

/**
 * @author Julien Plu
 */
public class InexistentNlpProcessExceptionTest {
  static final Logger LOGGER = LoggerFactory.getLogger(InexistentNlpProcessExceptionTest.class);

  public InexistentNlpProcessExceptionTest() {
  }

  /**
   * Test {@link InexistentNlpProcessException#InexistentNlpProcessException()} constructor.
   */
  @Test(expected = InexistentNlpProcessException.class)
  public final void testException1() {
    throw new InexistentNlpProcessException();
  }

  /**
   * Test {@link InexistentNlpProcessException#InexistentNlpProcessException(String, Throwable)}
   * constructor.
   */
  @Test(expected = InexistentNlpProcessException.class)
  public final void testException2() {
    throw new InexistentNlpProcessException("message", new Throwable());
  }

  /**
   * Test {@link InexistentNlpProcessException#InexistentNlpProcessException(Throwable)}
   * constructor.
   */
  @Test(expected = InexistentNlpProcessException.class)
  public final void testException3() {
    throw new InexistentNlpProcessException(new Throwable());
  }

  /**
   * Test that {@link InexistentNlpProcessException} is not serializable.
   */
  @Test(expected = NotSerializableException.class)
  public final void testNotSerializable() throws IOException {
    final InexistentNlpProcessException obj = new InexistentNlpProcessException();
    final ObjectOutput oos = new ObjectOutputStream(new ByteArrayOutputStream(0));

    oos.writeObject(obj);
  }

  /**
   * Test that {@link InexistentNlpProcessException} is not deserializable.
   */
  @Test(expected = EOFException.class)
  public final void testNotDeserializable() throws Exception {
    final InexistentNlpProcessException obj = new InexistentNlpProcessException();
    final Method method = obj.getClass().getDeclaredMethod("readObject", ObjectInputStream.class);

    method.setAccessible(true);
    method.invoke(obj, new ObjectInputStream(new ByteArrayInputStream("".getBytes(
        Charset.forName("UTF-8")))));
  }
}
