

class File

  class << self

    alias_method :open_without_vfs,        :open
    alias_method :mtime_without_vfs,       :mtime
    alias_method :stat_without_vfs,        :stat
    alias_method :exist_without_vfs?,      :exist?
    alias_method :directory_without_vfs?,  :directory?
    alias_method :file_without_vfs?,       :file?
    alias_method :expand_path_without_vfs, :expand_path


    def open(fname,mode_str='r', flags=nil, &block)
      if ( Fixnum === fname )
        return File.open_without_vfs( fname, mode_str, &block )
      end
      unless ( (String === fname ) && ( fname =~ /^vfszip:/ || fname =~ /^vfsfile:/ ) )
        return File.open_without_vfs(fname, mode_str, flags, &block )
      end
      file = org.jboss.virtual.VFS.root( fd )
      raise Errno::ENOENT unless file
      stream = file.openStream()
      io = stream.to_io
      result = io
      ( result = block.call( io ) ) if block
      result
    end

    def expand_path(*args)
      return args[0].to_s.dup if ( args[0] =~ /^(vfszip|vfsfile):/ )
      expand_path_without_vfs(*args) 
    end

    def mtime(filename)
      return mtime_without_vfs(filename) if ( File.exist_without_vfs?( filename ) )

      vfs_url, child_path = VFS.resolve_within_archive(filename)
      raise Errno::ENOENT.new unless vfs_url

      virtual_file = Java::OrgJbossVirtual::VFS.root( vfs_url )
      virtual_file = virtual_file.get_child( child_path ) if child_path

      Time.at( virtual_file.getLastModified() / 1000 )
    end

    def stat(filename)
      return stat_without_vfs(filename) if ( File.exist_without_vfs?( filename ) )

      vfs_url, child_path = VFS.resolve_within_archive(filename)
      raise Errno::ENOENT.new nil unless vfs_url

      virtual_file = Java::OrgJbossVirtual::VFS.root( vfs_url )
      virtual_file = virtual_file.get_child( child_path ) if child_path

      VFS::File::Stat.new( virtual_file )
    end

    def exist?(filename)
      return true if exist_without_vfs?( filename )

      vfs_url, child_path = VFS.resolve_within_archive(filename)
      return false unless vfs_url

      begin
        virtual_file = Java::OrgJbossVirtual::VFS.root( vfs_url )
        virtual_file = virtual_file.get_child( child_path ) if child_path
  
        return ( ( ! virtual_file.nil? ) && virtual_file.exists() )
      rescue Java::JavaIo::IOException => e
        return false
      end
    end

    def directory?(filename)
      return true if directory_without_vfs?( filename )
 
      vfs_url, child_path = VFS.resolve_within_archive(filename)
      return false unless vfs_url

      begin
        virtual_file = Java::OrgJbossVirtual::VFS.root( vfs_url )
        virtual_file = virtual_file.get_child( child_path ) if child_path
  
        return ( ( ! virtual_file.nil? ) && ( ! virtual_file.isLeaf() ) )
      rescue Java::JavaIo::IOException => e
        return false
      end
    end

    def file?(filename)
      return true if file_without_vfs?( filename )

      vfs_url, child_path = VFS.resolve_within_archive(filename)
      return false unless vfs_url

      begin
        virtual_file = Java::OrgJbossVirtual::VFS.root( vfs_url )
        virtual_file = virtual_file.get_child( child_path ) if child_path

        return ( ( ! virtual_file.nil? ) && ( virtual_file.isLeaf() ) )
      rescue Java::JavaIo::IOException => e
        return false
      end
    end

  end

end
