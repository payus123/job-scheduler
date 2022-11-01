package com.blusalt.dbxpbackgroundservice.tasks;

import com.blusalt.commons.exceptions.DbxpApplicationException;
import com.blusalt.dbxpbackgroundservice.models.enums.KnownHosts;
import com.blusalt.dbxpbackgroundservice.models.enums.SshAuthMode;
import com.blusalt.dbxpbackgroundservice.repository.TaskConfigRepository;
import com.blusalt.dbxpbackgroundservice.repository.TaskHistoryRepository;
import com.blusalt.dbxpbackgroundservice.tasks.params.ShellTaskParam;
import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

import static com.blusalt.dbxpbackgroundservice.models.enums.SshAuthMode.PRIVATE_KEY;
import static com.blusalt.dbxpbackgroundservice.util.validators.FileUtil.createKeyFile;
import static com.blusalt.dbxpbackgroundservice.util.validators.FileUtil.deleteKeyFile;

@Slf4j
public class ShellTask extends BlusaltTask {
    Gson gson = new Gson();
    String keyFileToDelete;

    public ShellTask(TaskConfigRepository taskConfigRepository, TaskHistoryRepository historyRepository) {
        super(taskConfigRepository, historyRepository);
    }

    @Override
    public boolean validateTaskParam() {
        return true;
    }

    @Override
    void runTask() throws DbxpApplicationException {
        try {
            ShellTaskParam param = gson.fromJson(getTaskParams(), ShellTaskParam.class);
            if (!KnownHosts.remoteHosts.containsKey(param.getSeverIp())) {
                throw new DbxpApplicationException("Host is not known");
            }
            String keyFile;
            String username = param.getUsername();
            Integer port = param.getPort();
            String key = param.getKeyPass().getKey();
            SshAuthMode authMode = SshAuthMode.valueOf(param.getKeyPass().getAuthMode());
            String directory = param.getScriptDirectory();
            String severIp = param.getSeverIp();
            keyFile = param.getUsername() + "@" + param.getSeverIp();
            keyFileToDelete = keyFile;
            JSch jSch = new JSch();
            Session session = jSch.getSession(username, severIp, port);
            if (authMode.equals(PRIVATE_KEY)) {
                createKeyFile(key, keyFile);
                jSch.addIdentity("src/main/resources/rsa_keys/" + keyFile);
            } else {
                session.setPassword(key);
            }

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");

            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand("bash " + directory);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);

            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    log.info("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
            log.info("DONE");

            deleteKeyFile(keyFile);
        } catch (Exception e) {
            deleteKeyFile(keyFileToDelete);
            throw new DbxpApplicationException(e.getMessage());
        }

    }
}
