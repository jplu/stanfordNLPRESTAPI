# -*- coding: utf-8 -*-

#
# StanfordNLPRESTAPI - Offering a REST API over Stanford CoreNLP to get results in NIF format.
# Copyright © 2017 Julien Plu (julien.plu@redaction-developpez.com)
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#

import urllib2
import zipfile
import progressbar

url = "http://adel.eurecom.fr/data/models-gazetteers-properties.zip"
bar = progressbar.ProgressBar()
file_name = url.split('/')[-1]
u = urllib2.urlopen(url)

with open(file_name, 'wb') as f:
    meta = u.info()
    file_size = int(meta.getheaders("Content-Length")[0])

    print ("Downloading: %s Bytes: %s" % (file_name, file_size))

    for i in bar(range(0, file_size, 8192)):
        buffer = u.read(8192)

        if not buffer:
            break

        f.write(buffer)

print ("Unzip " + file_name)

zip_ref = zipfile.ZipFile("models-gazetteers-properties.zip")
number_files = len(zip_ref.infolist())

bar.update(0)

for i in bar(range(number_files)):
    zip_ref.extract(zip_ref.infolist()[i])

zip_ref.close()
