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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Julien Plu
 */
public class InexistentNlpProcessException extends RuntimeException {
  static final Logger LOGGER = LoggerFactory.getLogger(InexistentNlpProcessException.class);
  private static final long serialVersionUID = 8554689862256764988L;

  public InexistentNlpProcessException() {
    super();
  }

  public InexistentNlpProcessException(final String message) {
    super(message);
  }

  public InexistentNlpProcessException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InexistentNlpProcessException(final Throwable cause) {
    super(cause);
  }

  private void writeObject(final ObjectOutputStream stream) throws NotSerializableException {
    throw new NotSerializableException("Impossible to serialize InexistentNlpProcessException");
  }

  private void readObject(final ObjectInputStream stream) throws EOFException {
    throw new EOFException("Improssible to deserialize InexistentNlpProcessException");
  }
}
