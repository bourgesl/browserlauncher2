//Copyright (c) 2005, Thomas Aglassinger
//All rights reserved.
//
//Redistribution and use in source and binary forms, with or without
//modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright
//notice, this list of conditions and the following disclaimer.
//
// * Redistributions in binary form must reproduce the above copyright
//notice, this list of conditions and the following disclaimer in the
//documentation and/or other materials provided with the distribution.
//
// * Neither the name of the author nor the names of its contributors
//may be used to endorse or promote products derived from this software
//without specific prior written permission.
//
//THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
//IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
//THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
//PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
//EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
//PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
//PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
//LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
//SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package net.sf.wraplog;

import java.io.PrintStream;

/**
 * Logger to write messages to <code>System.out</code> (debug, info) and
 * <code>System.err</code> (warn, error).
 * 
 * @see System#err
 * @see System#out
 * @author Thomas Aglassinger
 */
//$Id: SystemLogger.java,v 1.1 2005/02/24 21:16:04 roskakori Exp $
public class SystemLogger extends AbstractLogger {

	/* (non-Javadoc)
	 * @see edu.stanford.ejalbert.logging.Logger#log(int, java.lang.String, java.lang.Throwable)
	 */
	protected void log(int logLevel, String message, Throwable error) {
		PrintStream stream;
		if (logLevel < AbstractLogger.WARN) {
			stream = System.out;
		} else {
			stream = System.err;
		}
		if (message == null) {
			throw new NullPointerException("message must not be null");
		}
		if (stream == null) {
			throw new NullPointerException("stream must not be null");
		}
		stream.println(message);
		if (error != null) {
			error.printStackTrace(stream);
		}
	}
}