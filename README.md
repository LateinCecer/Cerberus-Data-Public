**Cerberus-Data**

**Def.:**
Cerberus Data is a simple, jet effective API to store and
 process raw and complex data with. It uses a tag system to
 save and read data. The file format is shown as following:
 
 Data types without a final size or a tag:
 `{short<Discriminator>, byte[]<RawData>}`
 
 Data types with a non-final size but no tag:
 `{short<Discriminator>, long<SizeInBytes>, byte[]<RawData>}`
 
 Data types with a final size and a tag:
 `{short<Discriminator>, String<Tag>, byte[]<RawData>}`
 
 Data types with a non-final size and a tag:
 `{short<Discriminator>, long<SizeInBytes, String<Tag>,
  byte[]<RawData>}`
  
 Each Data type has it's own builder class, with has to be
 passed to the Discriminator map of the IO-MetaStream to be
 able to read or write data. This, of cause, means that the
 discriminators of the different types of data can be easily
 modified (there is a high possibility for different programs
 to use different discriminators) which adds extra security
 and helps to make data processing more efficient by minimizing
 the actual amount of data types that can be written or read
 from/to an IO-Stream.
 
 Cerberus Data is a modular system with a dynamic range of
 data types, meaning that you can create and use your own
 data elements or tags with little effort. But don't worry,
 the most important and elementary instances of the MetaData
 interface are already implemented. Cerberus Data has, for
 example, data containers for Cerberus Math, a smaller Math
 library that is contained within almost all Cerberus related
 dependencies.