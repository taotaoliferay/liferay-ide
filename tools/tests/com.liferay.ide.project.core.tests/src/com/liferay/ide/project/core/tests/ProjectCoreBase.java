/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/
package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ZipUtil;
import com.liferay.ide.project.core.LiferayProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKManager;
import com.liferay.ide.sdk.core.SDKUtil;
import com.liferay.ide.server.tomcat.core.ILiferayTomcatRuntime;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.junit.Before;


/**
 * @author Gregory Amerson
 */
public abstract class ProjectCoreBase extends BaseTests
{
    protected final static IPath portalBundlesPath = new Path( System.getProperty(
        "liferay.bundles.dir", System.getProperty( "java.io.tmpdir" ) ) );

    protected SDK createNewSDK() throws Exception
    {
        final IPath newSDKLocation = new Path( getLiferayPluginsSdkDir().toString() + "-new" );

        if( ! newSDKLocation.toFile().exists() )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
        }

        assertEquals( true, newSDKLocation.toFile().exists() );

        SDK newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );

        if( newSDK == null )
        {
            FileUtils.copyDirectory( getLiferayPluginsSdkDir().toFile(), newSDKLocation.toFile() );
            newSDK = SDKUtil.createSDKFromLocation( newSDKLocation );
        }

        SDKManager.getInstance().addSDK( newSDK );

        return newSDK;
    }

    protected IProject createProject( NewLiferayPluginProjectOp op )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals(
            status.toString(),
            Status.createOkStatus().message().toLowerCase(), status.message().toLowerCase() );

        final IProject newLiferayPluginProject = project( op.getFinalProjectName().content() );

        assertNotNull( newLiferayPluginProject );

        assertEquals( true, newLiferayPluginProject.exists() );

        final IFacetedProject facetedProject = ProjectUtil.getFacetedProject( newLiferayPluginProject );

        assertNotNull( facetedProject );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

        assertNotNull( liferayFacet );

        final PluginType pluginTypeValue = op.getPluginType().content( true );

        if( pluginTypeValue.equals( PluginType.servicebuilder ) )
        {
            assertEquals( "liferay.portlet", liferayFacet.getId() );
        }
        else
        {
            assertEquals( "liferay." + pluginTypeValue, liferayFacet.getId() );
        }


        return newLiferayPluginProject;
    }

    @SuppressWarnings( "restriction" )
    protected IPath getCustomLocationBase()
    {
        final IPath customLocationBase =
            org.eclipse.core.internal.utils.FileUtil.canonicalPath( new Path( System.getProperty( "java.io.tmpdir" ) ) ).append(
                "custom-project-location-tests" );

        return customLocationBase;
    }

    protected IPath getIvyCacheZip()
    {
        return portalBundlesPath.append( "ivy-cache.zip" );
    }

    protected abstract IPath getLiferayPluginsSdkDir();

    protected abstract IPath getLiferayPluginsSDKZip();

    protected abstract String getLiferayPluginsSdkZipFolder();

    protected abstract IPath getLiferayRuntimeDir();

    protected abstract IPath getLiferayRuntimeZip();


    protected abstract String getRuntimeId();

    protected abstract String getRuntimeVersion();

    protected NewLiferayPluginProjectOp newProjectOp()
    {
        return NewLiferayPluginProjectOp.TYPE.instantiate();
    }

    protected NewLiferayPluginProjectOp newProjectOp( final String projectName ) throws Exception
    {
        final NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();
        op.setProjectName( projectName );

        return op;
    }

    /**
     * @throws Exception
     */
    @Before
    public void setupPluginsSDKAndRuntime() throws Exception
    {
        final File liferayPluginsSdkDirFile = getLiferayPluginsSdkDir().toFile();

        if( ! liferayPluginsSdkDirFile.exists() )
        {
            final File liferayPluginsSdkZipFile = getLiferayPluginsSDKZip().toFile();

            assertEquals(
                "Expected file to exist " + liferayPluginsSdkZipFile.getAbsolutePath(), true,
                liferayPluginsSdkZipFile.exists() );

            liferayPluginsSdkDirFile.mkdirs();

            final String liferayPluginsSdkZipFolder = getLiferayPluginsSdkZipFolder();

            if( CoreUtil.isNullOrEmpty( liferayPluginsSdkZipFolder ) )
            {
                ZipUtil.unzip( liferayPluginsSdkZipFile, liferayPluginsSdkDirFile );
            }
            else
            {
                ZipUtil.unzip(
                    liferayPluginsSdkZipFile, liferayPluginsSdkZipFolder, liferayPluginsSdkDirFile,
                    new NullProgressMonitor() );
            }
        }

        assertEquals( true, liferayPluginsSdkDirFile.exists() );

        final File ivyCacheDir = new File( liferayPluginsSdkDirFile, ".ivy" );

        if( ! ivyCacheDir.exists() )
        {
         // setup ivy cache

            final File ivyCacheZipFile = getIvyCacheZip().toFile();

            assertEquals( "Expected ivy-cache.zip to be here: " + ivyCacheZipFile.getAbsolutePath(), true, ivyCacheZipFile.exists() );

            ZipUtil.unzip( ivyCacheZipFile, liferayPluginsSdkDirFile );
        }

        assertEquals( "Expected .ivy folder to be here: " + ivyCacheDir.getAbsolutePath(), true, ivyCacheDir.exists() );

        SDK sdk = null;

        final SDK existingSdk = SDKManager.getInstance().getSDK( getLiferayPluginsSdkDir() );

        if( existingSdk == null )
        {
            sdk = SDKUtil.createSDKFromLocation( getLiferayPluginsSdkDir() );
        }
        else
        {
            sdk = existingSdk;
        }

        assertNotNull( sdk );

        sdk.setDefault( true );

        SDKManager.getInstance().setSDKs( new SDK[] { sdk } );

        final File liferayRuntimeDirFile = getLiferayRuntimeDir().toFile();

        if( ! liferayRuntimeDirFile.exists() )
        {
            final File liferayRuntimeZipFile = getLiferayRuntimeZip().toFile();

            assertEquals(
                "Expected file to exist " + liferayRuntimeZipFile.getAbsolutePath(), true,
                liferayRuntimeZipFile.exists() );

            ZipUtil.unzip( liferayRuntimeZipFile, LiferayProjectCore.getDefault().getStateLocation().toFile() );
        }

        assertEquals( true, liferayRuntimeDirFile.exists() );

        final NullProgressMonitor npm = new NullProgressMonitor();

        final String runtimeName = getRuntimeVersion();

        IRuntime runtime = ServerCore.findRuntime( runtimeName );

        if( runtime == null )
        {
            final IRuntimeWorkingCopy runtimeWC =
                ServerCore.findRuntimeType( getRuntimeId() ).createRuntime( runtimeName, npm );

            runtimeWC.setName( runtimeName );
            runtimeWC.setLocation( getLiferayRuntimeDir() );

            runtime = runtimeWC.save( true, npm );
        }

        assertNotNull( runtime );

        final ILiferayTomcatRuntime liferayRuntime =
            (ILiferayTomcatRuntime) ServerCore.findRuntime( runtimeName ).loadAdapter( ILiferayTomcatRuntime.class, npm );

        assertNotNull( liferayRuntime );

        final IPath customLocationBase = getCustomLocationBase();

        final File customBaseDir = customLocationBase.toFile();

        if( customBaseDir.exists() )
        {
            FileUtil.deleteDir( customBaseDir, true );

            if( customBaseDir.exists() )
            {
                for( File f : customBaseDir.listFiles() )
                {
                    System.out.println(f.getAbsolutePath());
                }
            }

            assertEquals( "Unable to delete pre-existing customBaseDir", false, customBaseDir.exists() );
        }
    }

}
