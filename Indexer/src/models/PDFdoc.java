/*
 * Copyright 2014 Mohd Azeem.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
/**
 *
 * @author Mohd Azeem
 */
public class PDFdoc {

    /**
     *
     * @param doc_path
     * @return
     * @throws Exception
     */
    public String extractPDF( String doc_path ) throws Exception
    {

        PDDocument document = null;
        try
        {
            document = PDDocument.load( doc_path );
            if( document.isEncrypted() )
            {
                try
                {
                    document.decrypt( "" );
                }
                catch( InvalidPasswordException e )
                {
                    System.err.println( "Error: Document is encrypted with a password." );
                    System.exit( 1 );
                }
            }

            PDFTextStripper stripper = new PDFTextStripper();
            String text="";
            text = stripper.getText(document);
//            System.out.println("text:"+text);
            return text;
        }
        finally
        {
            if( document != null )
            {
                document.close();
            }
        }

    }
}
