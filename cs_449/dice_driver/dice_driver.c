/*
* Zachary Whitney  zdw9  ID: 3320178
* CS 449 Summer 2018  Prof: Johnathan Misurda
* Recitation: Tues 9:30 AM
* Assignment #4, a craps client and linux device driver (d6 roller)
*
* This is simple driver which simulates a d6 device
* NOTE: This was written to run on the QEMU linux virtual machine (ARCH = i386)
*/

#include <linux/fs.h>
#include <linux/init.h>
#include <linux/miscdevice.h>
#include <linux/module.h>
#include <linux/random.h>

#include <asm/uaccess.h>

#define DIE_SIDES 6

/*
 * Simple helper function to get a random char from 0 to (max - 1)
 * Passing 6 gives numbers from 0 - 5
 */
unsigned char get_random_byte(int max) 
{
         unsigned char c;
         get_random_bytes(&c, 1);
         return c%max;
}

//This is the syscall implementation which actually "rolls dice"
static ssize_t read(struct file * file, char * buf, 
			  size_t count, loff_t *ppos)
{
	//Rolling a negative # of dice makes no sense
	if (count < 0)
		return -EINVAL;
	
	for(int i = 0; i < count; i++)
		buf[i] = get_random_byte(DIE_SIDES);

	//this file never ends
	*ppos += count;

	//Tell the user how much data we wrote.
	return count;
}

//The only file operation we care about is read
static const struct file_operations dice_fops = {
	.owner		= THIS_MODULE,
	.read		= read,
};

static struct miscdevice dice_driver = {
	/*
	 * We don't care what minor number we end up with, so tell the
	 * kernel to just pick one.
	 */
	MISC_DYNAMIC_MINOR,
	/*
	 * Name ourselves /dev/dice.
	 */
	"dice",
	/*
	 * What functions to call when a program performs file
	 * operations on the device.
	 */
	&dice_fops
};

static int __init
dice_init(void)
{
	int ret;

	/*
	 * Create the "dice" device in the /sys/class/misc directory.
	 * Udev will automatically create the /dev/dice device using
	 * the default rules.
	 */
	ret = misc_register(&dice_driver);
	if (ret)
		printk(KERN_ERR
		       "Unable to register \"dice\" misc device\n");

	return ret;
}

module_init(dice_init);

static void __exit
dice_exit(void)
{
	misc_deregister(&dice_driver);
}

module_exit(dice_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Zachary Whitney zdw9@pitt.edu ID 3320178");
MODULE_DESCRIPTION("Infinite d6 rolling module");
MODULE_VERSION("dev");
